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

data class BusStop(
    @SerializedName("arsId") val arsId: String,
    @SerializedName("name") val name: String,
    @SerializedName("gpsX") val longitude: Double,
    @SerializedName("gpsY") val latitude: Double
) : Parcelable { // Parcelable 인터페이스 구현 추가

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(arsId)
        parcel.writeString(name)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BusStop> {
        override fun createFromParcel(parcel: Parcel): BusStop {
            return BusStop(parcel)
        }

        override fun newArray(size: Int): Array<BusStop?> {
            return arrayOfNulls(size)
        }
    }
}

interface BusStopService {
    @Headers("Content-Type: application/json")
    @POST("/reservation/startst")
    fun getNearbyBusStops(@Body locationRequest: LocationRequest): Call<List<BusStop>>
}

data class LocationRequest(
    val gpsX: Double,
    val gpsY: Double
)

object ApiManager_BoardingBusStop {
    private const val BASE_URL = "https://your-api-base-url.com/"

    fun create(): BusStopService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(BusStopService::class.java)
    }
}
