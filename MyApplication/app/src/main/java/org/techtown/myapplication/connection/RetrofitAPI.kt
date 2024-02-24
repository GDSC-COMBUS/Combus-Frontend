package org.techtown.myapplication.connection

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RetrofitAPI {
    @GET("/reservation/bus")
    fun BusSelection(@Query("arsId") arsId : String): Call<List<RetrofitClient.ResponseBusSelection>>

    @Multipart
    @POST("/video")
    fun BusnumCamera(
        @Part videoFile: MultipartBody.Part,
        @Query("busRouteNm") busRouteNm: String?
        ): Call<RetrofitClient.ResponseCamera>
    @Multipart
    @POST("/image")
    fun BusnumPhoto(
        @Part file: MultipartBody.Part,
        @Query("busRouteNm") busRouteNm: String?
    ): Call<RetrofitClient.ResponseCamera>

}
