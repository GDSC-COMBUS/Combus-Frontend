package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.techtown.myapplication.Reservation
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class DropOffStop(
    @SerializedName("arsId") val arsId: String,
    @SerializedName("name") val name: String, //하차 버스 정류장 이름
    @SerializedName("direction") val direction: String,
    @SerializedName("gpsX") val longitude: Double,
    @SerializedName("gpsY") val latitude: Double,
    @SerializedName("seq") val seq: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(arsId)
        parcel.writeString(name) //하차 버스 정류장의 이름
        parcel.writeString(direction)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeInt(seq)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DropOffStop> {
        override fun createFromParcel(parcel: Parcel): DropOffStop {
            return DropOffStop(parcel)
        }

        override fun newArray(size: Int): Array<DropOffStop?> {
            return arrayOfNulls(size)
        }
    }
}

interface DropOffStopService {
    @Headers("Content-Type: application/json")
    @GET("/reservation/endst")
    fun getDropOffBusStops(): Call<List<DropOffStop>>
}

object ApiManager_DropOffBusStop {
    private const val BASE_URL = "https://your-api-base-url.com/"

    fun create(): DropOffStopService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(DropOffStopService::class.java)
    }
}


//예약완료 기능
// Reservation 클래스 파일
data class ReservationComplete(
    val boardingStop: String,
    val dropStop: String,
    val vehId: String,
    val busRouteNm: String
)

// ApiManager_Reservation 파일
interface ApiManager_ReservationComplete {
    @POST("/reservation")
    fun createReservation(@Body reservation: ReservationComplete): Call<ApiResponse>

    companion object {
        fun create(): ApiManager_ReservationComplete {
            val retrofit = Retrofit.Builder()
                .baseUrl("서버의_BASE_URL")  // 실제 서버의 BASE URL로 대체
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiManager_ReservationComplete::class.java)
        }
    }
}

data class ApiResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("code") val code: String,
    @SerializedName("status") val status: String,
    @SerializedName("detail") val detail: String
)



