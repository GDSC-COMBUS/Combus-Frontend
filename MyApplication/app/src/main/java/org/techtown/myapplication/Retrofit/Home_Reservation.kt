package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

data class HomeReservationResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("detail") val detail: String,
    @SerializedName("data") val data: ReservationData?
)

data class ReservationData(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("busId") val busId: Long,
    @SerializedName("status") val status: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("boardingStop") val boardingStop: String?,
    @SerializedName("dropStop") val dropStop: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(userId)
        parcel.writeLong(busId)
        parcel.writeString(status)
        parcel.writeString(createdAt)
        parcel.writeString(boardingStop)
        parcel.writeString(dropStop)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReservationData> {
        override fun createFromParcel(parcel: Parcel): ReservationData {
            return ReservationData(parcel)
        }

        override fun newArray(size: Int): Array<ReservationData?> {
            return arrayOfNulls(size)
        }
    }
}

interface HomeReservationService {
    @GET("users/home")
    fun getHomeReservation(): Call<HomeReservationResponse>

    @PUT("update-reservation-status")
    fun updateReservationStatus(@Query("reservationId") reservationId: Long?, @Query("newStatus") newStatus: String): Call<HomeReservationResponse>
}

class ApiManager_homeReservation {
    companion object {
        private const val BASE_URL = "<https://your-api-base-url.com/>"

        fun create(): HomeReservationService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(HomeReservationService::class.java)
        }

        // 예약 상태 업데이트 함수 추가
        fun updateReservationStatus(reservationId: Long?, newStatus: String): Call<HomeReservationResponse> {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(HomeReservationService::class.java)
            return service.updateReservationStatus(reservationId, newStatus)
        }
    }
}

