package com.cook.dmnet

import okhttp3.Interceptor

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
object NetConfig {
    var baseUrl: String = ""
    var timeOut: Long = 10L
    var enableLog: Boolean = true
    var commonHeader: Map<String, String>? = null
    var interceptorList: MutableList<Interceptor> = mutableListOf()
    var logHelper: LogHelper? = null

    fun init(
        baseUrl: String,
        log: (msg: String) -> Unit
    ) {
        this.baseUrl = baseUrl
        this.logHelper = object : LogHelper {
            override fun log(msg: String) {
                log.invoke(msg)
            }
        }
    }


    fun init(
        baseUrl: String,
        commonHeader: Map<String, String>,
        interceptor: List<Interceptor>,
        log: (msg: String) -> Unit
    ) {
        this.baseUrl = baseUrl
        this.commonHeader = commonHeader
        interceptorList.addAll(interceptor)
        this.logHelper = object : LogHelper {
            override fun log(msg: String) {
                log.invoke(msg)
            }

        }
    }


    fun init(
        baseUrl: String,
        timeout: Long,
        enableLog: Boolean,
        commonHeader: Map<String, String>,
        logHelper: LogHelper
    ) {
        this.baseUrl = baseUrl
        this.timeOut = timeout
        this.enableLog = enableLog
        this.commonHeader = commonHeader

        this.logHelper = logHelper
    }
}


interface LogHelper {
    fun log(msg: String)
}
