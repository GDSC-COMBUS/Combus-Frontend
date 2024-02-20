package com.example.combus_driverapp.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitAPI {
    @POST("/drivers/login")
    fun login(@Body request:RetrofitClient.requestlogin): Call<RetrofitClient.responselogin>

    @GET("/drivers/home/{driverId}")
    fun home(@Path("driverId") userId:Long):Call<RetrofitClient.responsehome>

    @GET("/drivers/home/busStop/{arsId}")
    fun busstopDetail(@Path ("arsId") arsId:String): Call<RetrofitClient.responsebusstopDetail>
}