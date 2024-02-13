package org.techtown.myapplication.connection

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/reservation/bus")
    fun BusSelection(@Query("arsId") arsId : String): Call<List<RetrofitClient.ResponseBusSelection>>
}
