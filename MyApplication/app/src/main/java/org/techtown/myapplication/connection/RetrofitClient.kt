package org.techtown.myapplication.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {
    abstract class responseBusSelection(
        @SerializedName("vehId")
        val vehId:String,
        @SerializedName("busRouteId")
        val busRouteId:String,
        @SerializedName("busRouteAbrv")
        val busRouteAbrv:String,
        @SerializedName("low")
        val low:Boolean
    ) : List<responseBusSelection>
}