package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

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
}

class ApiManager_homeReservation {
    companion object {
        private const val BASE_URL = "<https://your-api-base-url.com/>" // 실제 API의 베이스 URL로 변경

        fun create(): HomeReservationService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(HomeReservationService::class.java)
        }
    }
}
