package com.cook.dmnet.interceptor

import com.cook.dmnet.NetConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
class HeadInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val headers = NetConfig.commonHeader
        val requestBuilder = original.newBuilder()
        headers?.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
