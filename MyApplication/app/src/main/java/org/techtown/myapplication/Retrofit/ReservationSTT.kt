package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ReservationSTTRequest(
    @SerializedName("userId") val userId: Long?,
    @SerializedName("boardingStop") val boardingStop: String?,
    @SerializedName("dropStop") val dropStop: String?,
    @SerializedName("busRouteNm") val busRouteNm: String?
)

interface ReservationSTTService {
    @Headers("Content-Type: application/json")
    @POST("reservation/stt")
    fun makeReservation(
        @Body request: ReservationSTTRequest  // 매개변수 타입을 ReservationSTTRequest로 변경
    ): Call<Void>
}

object ApiManager_ReservationSTT {
    private const val BASE_URL = "http://34.64.189.150:8090/"

    fun create(): ReservationSTTService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ReservationSTTService::class.java)
    }
}
