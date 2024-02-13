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
        @SerializedName("driverId")
        val driverId:Long,
        @SerializedName("driverName")
        val driverName:String,
        @SerializedName("loginId")
        val loginId:String
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
        @SerializedName("busPosDto")
        val busPosDto:homebusPos,
        @SerializedName("busStopList")
        val busStopList:List<RetrofitClient.homebusStopList>
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
    data class responsebusstopDetail(
        @SerializedName("timestamp")
        val timestamp:String,
        @SerializedName("code")
        val code:String,
        @SerializedName("status")
        val status:String,
        @SerializedName("detail")
        val detail:String,
        @SerializedName("data")
        val data:busstopDetaildata
    )
    data class busstopDetaildata(
        @SerializedName("boardingInfo")
        val boardingInfo:List<detailInfolist>,
        @SerializedName("boardingBlindCnt")
        val boardingBlindCnt:Int,
        @SerializedName("boardingWheelchairCnt")
        val boardingWheelchairCnt:Int,
        @SerializedName("dropInfo")
        val dropInfo:List<detailInfolist>,
        @SerializedName("dropBlindCnt")
        val dropBlindCnt:Int,
        @SerializedName("dropWheelchairCnt")
        val dropWheelchairCnt:Int
    )
    data class detailInfolist(
        @SerializedName("type")
        val type:String,
        @SerializedName("boardingStop")
        val boardingStop:String,
        @SerializedName("dropStop")
        val dropStop:String
    )

}