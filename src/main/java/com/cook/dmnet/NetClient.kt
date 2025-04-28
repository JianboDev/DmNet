package com.cook.dmnet

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
object NetClient {

    fun <T> create(service: Class<T>): T {
        if (NetConfig.baseUrl.isEmpty()) {
            throw IllegalArgumentException("BaseUrl can not be empty")
        }
        return RetrofitFactory.instance.create(service)
    }
}
