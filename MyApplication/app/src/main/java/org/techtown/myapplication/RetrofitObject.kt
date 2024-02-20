package org.techtown.myapplication

import okhttp3.OkHttpClient
import org.techtown.myapplication.connection.RetrofitAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {
    private const val BASE_URL = "http://34.64.189.150:8090"

    private val getRetrofit by lazy {
        val clientBuilder = OkHttpClient.Builder()

        clientBuilder.connectTimeout(180000, TimeUnit.MILLISECONDS)
        clientBuilder.readTimeout(120, TimeUnit.SECONDS)
        clientBuilder.writeTimeout(120,TimeUnit.SECONDS)

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}