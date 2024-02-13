package com.example.combus_driverapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.ActivityDriverHomeBinding
import com.google.gson.JsonParseException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.properties.Delegates

class Driver_Home : AppCompatActivity() {
    private lateinit var binding: ActivityDriverHomeBinding
    private val handler = Handler()
    private val interval: Long = 30 * 1000
    private lateinit var adapter: busstop_list_adapter // 어댑터 추가
    private var arsId:Long = 0
    private var stSeq:Int = 0
    private var stopFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 어댑터 초기화 및 리사이클러뷰에 설정
        adapter = busstop_list_adapter(emptyList(),stSeq,stopFlag )
        binding.bussropRecycle.adapter = adapter

        fetchFromServer()

    }
    private fun fetchFromServer() {
        handler.post(object : Runnable {
            override fun run() {
                val extras = intent.extras
                val driverId: Long = extras?.getLong("driverId", -1L) ?: -1L
                if (driverId == -1L) {
                    Log.e("Driver_Home", "Driver ID is not provided or empty.")
                    return
                }
                Log.d("data", driverId.toString())
                // Retrofit을 사용하여 서버에 요청을 보냅니다.

                val call = RetrofitObject.getRetrofitService.home(driverId)
                call.enqueue(object : Callback<RetrofitClient.responsehome>{
                    override fun onResponse(
                        call: Call<RetrofitClient.responsehome>,
                        response: Response<RetrofitClient.responsehome>
                    ) {
                        if (response.isSuccessful){
                            Log.d("response","ok")
                            val response = response.body()
                            if (response != null){
                                if (response.status == "OK"){
                                    val busstopdata = response.data.busStopList
                                    //val busPosdata = listOf(response.data.busPos)
                                    arsId = response.data.busPosDto.arsId
                                    stSeq = response.data.busPosDto.stSeq
                                    stopFlag = response.data.busPosDto.stopFlag
                                    Log.d("data",busstopdata.toString())
                                    Log.d("data",arsId.toString())
                                    Log.d("data",stSeq.toString())
                                    Log.d("data",stopFlag.toString())

                                    adapter.updateData(busstopdata,stSeq,stopFlag)
                                    binding.bussropRecycle.adapter = busstop_list_adapter(busstopdata,stSeq,stopFlag)
                                    binding.txtBusnum.text = response.data.busRouteName
                                    binding.txtBusBookNum.text = "예약 ${response.data.totalReserved}"
                                    binding.txtBusOutNum.text = "하차 ${response.data.totalDrop}"

                                    val layoutManager = LinearLayoutManager(this@Driver_Home)
                                    binding.bussropRecycle.layoutManager = layoutManager
                                    val centerOfScreen: Int = binding.bussropRecycle.height / 2
                                    val stSeqInt = response.data.busPosDto?.stSeq
                                    if (stSeqInt != null) {
                                        layoutManager.scrollToPositionWithOffset(stSeqInt, centerOfScreen)
                                    } else {
                                        Log.e("NullPointerException", "busPos is null or stSeq is null")
                                    }
                                    /*layoutManager.scrollToPositionWithOffset(
                                        response.data.busPos.stSeq,
                                        centerOfScreen
                                    )*/
                                }else{
                                    Toast.makeText(this@Driver_Home,response.detail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else {
                            // 서버로부터 응답을 받지 못했을 때 처리
                            Log.d("Retrofit", "false")
                        }
                    }
                    override fun onFailure(call: Call<RetrofitClient.responsehome>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message} "
                        Log.d("Retrofit", errorMessage)
                    }
                })
                // 다음 요청을 예약합니다.
                handler.postDelayed(this, interval)

            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        // 액티비티가 종료될 때 핸들러 작업을 제거하여 메모리 누수를 방지합니다.
        handler.removeCallbacksAndMessages(null)
    }
}