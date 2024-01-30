package org.techtown.myapplication

import okhttp3.OkHttpClient
import org.techtown.myapplication.connection.RetrofitAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {
    private const val BASE_URL = ""

    private val getRetrofit by lazy {
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.readTimeout(30, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(30,TimeUnit.SECONDS)

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}