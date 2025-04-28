package com.cook.dmnet

import com.cook.dmnet.interceptor.HttpLoggingInterceptor
import com.cook.dmnet.interceptor.HeadInterceptor
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*
/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
internal object OkHttpFactory {

    fun create(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(NetConfig.timeOut, TimeUnit.SECONDS)
            .readTimeout(NetConfig.timeOut, TimeUnit.SECONDS)
            .writeTimeout(NetConfig.timeOut, TimeUnit.SECONDS)
            .addInterceptor(HeadInterceptor())

        if (NetConfig.enableLog) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }

        val sslSocketFactory = createSSLSocketFactory()
        if (sslSocketFactory != null) {
            builder.sslSocketFactory(sslSocketFactory, TrustAllCerts())
            builder.hostnameVerifier { _, _ -> true }
        }

        return builder.build()
    }

    private fun createSSLSocketFactory(): SSLSocketFactory? {
        return try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom())
            sc.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private class TrustAllCerts : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}
