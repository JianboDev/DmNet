package com.cook.dmnet

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2020/8/28
 *    @Desc   :
 */
data class BaseResponse<out T>(
    val code: Int = 0,
    val message: String = "",
    val data: T = Any() as T
)
