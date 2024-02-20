package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
    fun getDropOffBusStops(
        @Query("arsId") arsId: String?,  // 올바른 쿼리 매개변수 이름 사용
        @Query("busRouteId") busRouteId: String?  // 올바른 쿼리 매개변수 이름 사용
    ): Call<List<DropOffStop>>
}


data class ReservationRequest(
    val arsId: String,
    val busRouteId: String
)

object ApiManager_DropOffBusStop {
    private const val BASE_URL = "http://34.64.189.150:8090/"

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
    val userId:Long?,
    val boardingStop: String?, //승차정류장 고유 번호
    val dropStop: String?, //하차정류장 고유 번호
    val vehId: String?, //버스 Id
    val busRouteNm: String? //버스 노선 번호
)

// ApiManager_Reservation 파일
interface ApiManager_ReservationComplete {
    @POST("/reservation/")
    fun createReservation(@Body reservation: ReservationComplete): Call<ApiResponse>

    companion object {
        private const val BASE_URL = "http://34.64.189.150:8090/"

        fun create(): ApiManager_ReservationComplete {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)  // 실제 서버의 BASE URL로 대체
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
