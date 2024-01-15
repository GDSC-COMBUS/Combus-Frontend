package org.techtown.myapplication

import retrofit2.Call
import retrofit2.http.GET

//API 호출을 위한 Retrofit Interface 추가
interface HomeService {
    @GET("users/home")
    fun getReservationStatus(): Call<ReservationResponse>
}

data class ReservationResponse(
    val timestamp: String,
    val code: String,
    val status: String,
    val detail: String,
    val data: ReservationData?
)

data class ReservationData(
    val id: Long,
    val userId: Long,
    val busId: Long,
    val status: String,
    val createdAt: String,
    val boardingStop: Long,
    val dropStop: Long
)
