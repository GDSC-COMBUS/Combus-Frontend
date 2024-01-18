package com.example.combus_driverapp.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {
    data class requestlogin(
        @SerializedName("loginId")
        val loginId:String
    )
    data class responselogin(
        @SerializedName("timestamp")
        val timestamp:String,
        @SerializedName("code")
        val code:String,
        @SerializedName("status")
        val status:String,
        @SerializedName("detail")
        val detail:String,
        @SerializedName("data")
        val data:logindata
    )
    data class logindata(
        @SerializedName("sessionId")
        val sessionId:String,
        @SerializedName("driverId")
        val driverId:Long,
        @SerializedName("driverName")
        val driverName:String,
        @SerializedName("loginId")
        val loginIn:String
    )
}