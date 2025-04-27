package com.cook.dmnet.interceptor

import com.cook.dmnet.NetConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2020/9/17
 *    @Desc   :
 */
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val requestBuilder: Request.Builder = original.newBuilder()
            .header("token", NetConfig.token)
            .header("Platform", NetConfig.platform)
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }
}