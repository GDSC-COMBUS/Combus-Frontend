package org.techtown.myapplication.connection

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

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
    data class RequestCamera(
        @SerializedName("videoFile")
        val videoFile:MultipartBody.Part
    )
    data class ResponseCamera(
        @SerializedName("timestamp")
        val timestamp:String,
        @SerializedName("code")
        val code:String,
        @SerializedName("status")
        val status:String,
        @SerializedName("detail")
        val detail:String,
        @SerializedName("data")
        val data:cameradata
    )
    data class cameradata(
        @SerializedName("correct")
        val correct:Boolean
    )
}
