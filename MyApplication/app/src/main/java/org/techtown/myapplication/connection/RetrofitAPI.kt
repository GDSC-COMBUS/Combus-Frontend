package org.techtown.myapplication.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/reservation/bus")
    fun BusSelection(@Query("arsId") arsId : String): Call<List<RetrofitClient.ResponseBusSelection>>

    @POST("/video")
    fun BusnumCamera(
        @Body requestBody:RetrofitClient.RequestCamera,
        @Query("busRouteNm") busRouteNm: String?
        ): Call<RetrofitClient.ResponseCamera>
}
