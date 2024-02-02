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
    data class requesthome(
        @SerializedName("driverId")
        val driverId:Long
    )
    data class responsehome(
        @SerializedName("timestamp")
        val timestamp:String,
        @SerializedName("code")
        val code:String,
        @SerializedName("status")
        val status:String,
        @SerializedName("detail")
        val detail:String,
        @SerializedName("data")
        val data:homedata
    )
    data class homedata(
        @SerializedName("vehId")
        val vehId:Long,
        @SerializedName("busRouteName")
        val busRouteName:String,
        @SerializedName("totalReserved")
        val totalReserved:Int,
        @SerializedName("totalDrop")
        val totalDrop:Int,
        @SerializedName("busPos")
        val busPos:homebusPos,
        @SerializedName("busStopList")
        val busStopList:homebusStopList
    )
    data class homebusPos(
        @SerializedName("arsId")
        val arsId:Long,
        @SerializedName("stSeq")
        val stSeq:Int,
        @SerializedName("stopFlag")
        val stopFlag:Boolean
    )
    data class homebusStopList(
        @SerializedName("arsId")
        val arsId: Long,
        @SerializedName("name")
        val name:String,
        @SerializedName("gpsX")
        val gpsX:Double,
        @SerializedName("gpsY")
        val gpsY:Double,
        @SerializedName("seq")
        val seq:Int,
        @SerializedName("reserved_cnt")
        val reserved_cnt:Int,
        @SerializedName("drop_cnt")
        val drop_cnt:Int,
        @SerializedName("wheelchair")
        val wheelchair:Boolean
    )
}