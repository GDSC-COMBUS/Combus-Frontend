package org.techtown.myapplication.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {
    data class ResponseBusSelection(
        @SerializedName("vehId")
        val vehId: String,
        @SerializedName("busRouteId")
        val busRouteId: String,
        @SerializedName("busRouteAbrv")
        val busRouteAbrv: String,
        @SerializedName("low")
        val low: Int // Int 타입으로 변경
    ) {
        // 필요한 경우 boolean으로 변환하는 메서드 추가
        fun isLow(): Boolean {
            return low != 0
        }
    }
}
