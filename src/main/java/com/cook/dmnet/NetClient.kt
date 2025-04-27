package com.cook.dmnet

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
object NetClient {

    fun <T> create(service: Class<T>): T {
        return RetrofitFactory.instance.create(service)
    }
}
