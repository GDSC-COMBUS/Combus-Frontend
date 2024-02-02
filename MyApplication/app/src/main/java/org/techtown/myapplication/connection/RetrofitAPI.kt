package org.techtown.myapplication.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface RetrofitAPI {
    @GET("/reservation/bus")
    fun BusSelection(@Body request: RetrofitClient.requestBusSelection): Call<RetrofitClient.responseBusSelection>
}