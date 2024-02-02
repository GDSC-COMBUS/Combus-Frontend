package com.example.combus_driverapp.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitAPI {
    @POST("/drivers/login")
    fun login(@Body request:RetrofitClient.requestlogin): Call<RetrofitClient.responselogin>

    @GET("/drivers/home")
    fun home():Call<RetrofitClient.responsehome>
}