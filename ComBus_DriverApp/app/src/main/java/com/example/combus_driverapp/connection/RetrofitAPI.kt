package com.example.combus_driverapp.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitAPI {
    @POST("/drivers/login")
    fun login(@Body request:RetrofitClient.requestlogin): Call<RetrofitClient.responselogin>

    @GET("/drivers/home")
    fun home():Call<RetrofitClient.responsehome>

    @GET("/drivers/home/{arsId}")
    fun busstopDetail(@Path ("arsId") arsId:Int): Call<RetrofitClient.responsebusstopDetail>
}