package com.example.combus_driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.ActivityDriverHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Driver_Home : AppCompatActivity() {
    private lateinit var binding: ActivityDriverHomeBinding
    private val handler = Handler()
    private val interval: Long = 30 * 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = intent.extras
        val driverId = extras!!["driverId"]

        fetchFromServer()

        initializeViews()

    }
    private fun fetchFromServer() {
        handler.post(object : Runnable {
            override fun run() {
                // Retrofit을 사용하여 서버에 요청을 보냅니다.
                val retrofitService = RetrofitObject.getRetrofitService
                val call = retrofitService.home()

                call.enqueue(object : Callback<RetrofitClient.responsehome> {
                    override fun onResponse(call: Call<RetrofitClient.responsehome>, response: Response<RetrofitClient.responsehome>) {
                        if (response.isSuccessful) {
                            // 서버로부터 응답을 성공적으로 받았을 때 처리
                            val responseData = response.body()
                            responseData?.let {
                                // 데이터를 사용하여 UI 업데이트 또는 처리 작업 수행
                                val busstopdata: List<RetrofitClient.homebusStopList> =
                                    (responseData.data.busStopList) as List<RetrofitClient.homebusStopList>
                                val busPosdata: List<RetrofitClient.homebusPos> = listOf(
                                    responseData.data.busPos
                                )
                                binding.bussropRecycle.adapter = busstop_list_adapter(busstopdata,busPosdata)
                                binding.txtBusnum.text = responseData.data.busRouteName
                                binding.txtBusBookNum.text = "예약 ${responseData.data.totalReserved}"
                                binding.txtBusOutNum.text = "하차 ${responseData.data.totalDrop}"
                            }
                        } else {
                            // 서버로부터 응답을 받지 못했을 때 처리
                        }
                    }

                    override fun onFailure(call: Call<RetrofitClient.responsehome>, t: Throwable) {
                        // 네트워크 오류 등의 이유로 서버에 요청을 보내지 못했을 때 처리
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
    private fun initializeViews(){
        binding.bussropRecycle.layoutManager = LinearLayoutManager(this)
    }
}