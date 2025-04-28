package com.cook.dmnet

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
data class BaseResponse<out T>(
    val code: Int = 0,
    val message: String = "",
    val data: T = Any() as T
)
