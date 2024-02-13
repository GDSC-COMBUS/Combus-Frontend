package org.techtown.myapplication.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitAPI {
    @GET("/reservation/bus/{strSrch}")
    fun BusSelection(@Path ("strSrch") strSrch:String): Call<RetrofitClient.responseBusSelection>
}