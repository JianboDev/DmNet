package com.cook.dmnet


import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date

/**
 *    @Author : Ｃｏｏｋ
 *    @Date   : 2025/4/27
 *    @Desc   :
 *    @Version:
 */
internal object RetrofitFactory {

    val instance: Retrofit by lazy {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, JsonDeserializer<Date> { json, _, _ ->
                json?.asString?.let { parseDate(it) }
            })
            .create()
        Retrofit.Builder()
            .baseUrl(NetConfig.baseUrl)
            .client(OkHttpFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}



private fun parseDate(dateStr: String): Date? {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return simpleDateFormat.parse(dateStr)
}
