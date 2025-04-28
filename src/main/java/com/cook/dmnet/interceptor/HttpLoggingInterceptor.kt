package com.cook.dmnet.interceptor


import com.cook.dmnet.NetConfig
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.promisesBody
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.Locale
import java.util.concurrent.TimeUnit
/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
class HttpLoggingInterceptor() : Interceptor {
    @Volatile
    private var printLevel: Level = Level.NONE
    private var colorLevel: java.util.logging.Level? = null


    enum class Level {
        NONE,  //不打印log
        BASIC,  //只打印 请求首行 和 响应首行
        HEADERS,  //打印请求和响应的所有 Header
        BODY //所有数据全部打印
    }

    fun setPrintLevel(level: Level) {
        printLevel = level
    }

    fun setColorLevel(level: java.util.logging.Level?) {
        colorLevel = level
    }

    private fun log(message: String) {
        NetConfig.logHelper?.log(message)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (printLevel == Level.NONE) {
            return chain.proceed(request)
        }

        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        val startNs = System.nanoTime()
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        //响应日志拦截
        return logForResponse(response, tookMs)
    }

    @Throws(IOException::class)
    private fun logForRequest(request: Request, connection: Connection?) {
        val logBody = printLevel == Level.BODY
        val logHeaders = printLevel == Level.BODY || printLevel == Level.HEADERS
        val requestBody = request.body
        val hasRequestBody = requestBody != null
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1
        try {
            val requestStartMessage = "--> " + request.method + ' ' + request.url + ' ' + protocol
            log(requestStartMessage)
            if (logHeaders) {
                if (hasRequestBody) {
                    // Request body headers are only present when installed as a network interceptor. Force
                    // them to be included (when available) so there values are known.
                    if (requestBody.contentType() != null) {
                        log("\tContent-Type: " + requestBody.contentType())
                    }
                    if (requestBody.contentLength() != -1L) {
                        log("\tContent-Length: " + requestBody.contentLength())
                    }
                }
                val headers = request.headers
                var i = 0
                val count = headers.size
                while (i < count) {
                    val name = headers.name(i)
                    // Skip headers from the request body as they are explicitly logged above.
                    if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(
                            name,
                            ignoreCase = true
                        )
                    ) {
                        log("\t" + name + ": " + headers.value(i))
                    }
                    i++
                }
                log(" ")
                if (logBody && hasRequestBody) {
                    if (isPlaintext(requestBody!!.contentType())) {
                        bodyToString(request)
                    } else {
                        log("\tbody: ")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            log("--> END " + request.method)
        }
    }

    private fun logForResponse(response: Response, tookMs: Long): Response {
        val builder = response.newBuilder()
        val clone = builder.build()
        var responseBody = clone.body
        val logBody = printLevel == Level.BODY
        val logHeaders = printLevel == Level.BODY || printLevel == Level.HEADERS
        try {
            log("<-- " + clone.code + ' ' + clone.message + ' ' + clone.request.url + " (" + tookMs + "ms)")
            if (logHeaders) {
                val headers = clone.headers
                var i = 0
                val count = headers.size
                while (i < count) {
                    log("\t" + headers.name(i) + ": " + headers.value(i))
                    i++
                }
                log(" ")
                if (logBody && response.promisesBody()) {
                    if (responseBody == null) return response
                    if (isPlaintext(responseBody.contentType())) {
                        val bytes = IOUtils.toByteArray(responseBody.byteStream())
                        val contentType = responseBody.contentType()
                        val body = String(bytes, getCharset(contentType)!!)
                        log("\tbody:$body")
                        responseBody = bytes.toResponseBody(responseBody.contentType())
                        return response.newBuilder().body(responseBody).build()
                    } else {
                        log("\tbody: null")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            log("<-- END HTTP")
        }
        return response
    }

    private fun bodyToString(request: Request) {
        try {
            val copy = request.newBuilder().build()
            val body = copy.body ?: return
            val buffer = Buffer()
            body.writeTo(buffer)
            val charset = getCharset(body.contentType())
            log("\tbody:" + buffer.readString(charset!!))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
        private fun getCharset(contentType: MediaType?): Charset? {
            var charset = if (contentType != null) contentType.charset(UTF8) else UTF8
            if (charset == null) charset = UTF8
            return charset
        }

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        private fun isPlaintext(mediaType: MediaType?): Boolean {
            if (mediaType == null) return false
            if (mediaType.type == "text") {
                return true
            }
            var subtype = mediaType.subtype
            subtype = subtype.lowercase(Locale.ROOT)
            //
            return subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains(
                "xml"
            ) || subtype.contains("html")
        }
    }

}