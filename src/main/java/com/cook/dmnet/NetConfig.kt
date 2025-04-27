package com.cook.dmnet

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
    var token: String = ""
    var platform: String = ""

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
        platform: String,
        log: (msg: String) -> Unit
    ) {
        this.baseUrl = baseUrl
        this.platform = platform
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
        token: String,
        platform: String,
        logHelper: LogHelper
    ) {
        this.baseUrl = baseUrl
        this.timeOut = timeout
        this.enableLog = enableLog

        this.token = token
        this.platform = platform
        this.logHelper = logHelper
    }
}


interface LogHelper {
    fun log(msg: String)
}
