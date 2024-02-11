package org.techtown.myapplication.Retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


class HeaderInterceptor(private val cookie: String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = original.newBuilder()

        // 쿠키 값이 있을 경우에만 헤더에 추가
        cookie?.let {
            requestBuilder.header("Cookie", it)
        }

        val request = requestBuilder.method(original.method, original.body).build()
        return chain.proceed(request)
    }
}

data class HomeReservationResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("code") var code: String,
    @SerializedName("status") val status: String,
    @SerializedName("detail") val detail: String,
    @SerializedName("data") val data: ReservationData?
)

data class ReservationData(
    @SerializedName("id") val id: Long,
    @SerializedName("date") val date: String, // 수정된 필드
    @SerializedName("busRouteName") val busRouteName: String, // 수정된 필드
    @SerializedName("boardingStop") val boardingStop: String,
    @SerializedName("dropStop") val dropStop: String,
    @SerializedName("status") val status: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(date)
        parcel.writeString(busRouteName)
        parcel.writeString(boardingStop)
        parcel.writeString(dropStop)
        parcel.writeString(status)
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
    @GET("users/home/{userId}") // URL 경로에 userId 포함
    fun getHomeReservation(@Path("userId") userId: Long?): Call<HomeReservationResponse>

    @PUT("update-reservation-status")
    fun updateReservationStatus(@Query("reservationId") reservationId: Long?, @Query("newStatus") newStatus: String): Call<HomeReservationResponse>
}

class ApiManager_homeReservation {
    companion object {
        private const val BASE_URL = "http://34.64.189.150:8090/"

        // 이전 로그인 API에서 받은 쿠키값
        private var userCookie: String? = null

        // Retrofit 객체를 한 번만 생성하도록 변경
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun create(): HomeReservationService {
            // 헤더에 쿠키 추가
            val headerInterceptor = HeaderInterceptor(userCookie)
            val client = OkHttpClient.Builder().addInterceptor(headerInterceptor).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(HomeReservationService::class.java)
        }

        // 쿠키 값을 설정하는 메서드 추가
        fun setUserCookie(cookie: String?) {
            userCookie = cookie
        }

        fun updateReservationStatus(reservationId: Long?, newStatus: String): Call<HomeReservationResponse> {
            val service = create()
            return service.updateReservationStatus(reservationId, newStatus)
        }
    }
}
