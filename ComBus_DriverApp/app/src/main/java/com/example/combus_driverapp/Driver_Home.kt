package com.example.combus_driverapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import java.util.*
import kotlin.properties.Delegates

class Driver_Home : AppCompatActivity() {
    private lateinit var binding: ActivityDriverHomeBinding
    private val handler = Handler()
    private val interval: Long = 30 * 1000
    private lateinit var adapter: busstop_list_adapter // 어댑터 추가
    private var arsId:Long = 0L
    private var stSeq:Int = 0
    private var stopFlag:Boolean = false

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
                                    //val filteredBusStopData = busstopdata.filterNot { it.arsId.toString().isNullOrBlank() }
                                    //val busPosdata = listOf(response.data.busPos)
                                    arsId = response.data.busPosDto.arsId?: 0L
                                    stSeq = response.data.busPosDto.stSeq?.toInt() ?: 0
                                    stopFlag = response.data.busPosDto.stopFlag
                                    Log.d("data",busstopdata.toString())
                                    Log.d("data",arsId.toString())
                                    Log.d("data",stSeq.toString())
                                    Log.d("data",stopFlag.toString())

                                    adapter.updateData(busstopdata,stSeq,stopFlag)
                                    binding.bussropRecycle.adapter = busstop_list_adapter(busstopdata,stSeq,stopFlag)
                                    binding.txtBusnum.text = response.data.busRouteName
                                    binding.txtBusBookNum.text = "Reservation ${response.data.totalReserved}"
                                    binding.txtBusOutNum.text = "Dropping off ${response.data.totalDrop}"

                                    val layoutManager = LinearLayoutManager(this@Driver_Home)
                                    binding.bussropRecycle.layoutManager = layoutManager
                                    var stSeqInt = response.data.busPosDto?.stSeq
                                    var centerOfScreen: Int = binding.bussropRecycle.height / 2

                                    if (response.data.busPosDto?.stSeq!! < 3){
                                        centerOfScreen = binding.bussropRecycle.height / 4
                                        if (stSeqInt != null) {
                                            layoutManager.scrollToPositionWithOffset(stSeqInt, centerOfScreen)
                                        } else {
                                            Log.e("NullPointerException", "busPos is null or stSeq is null")
                                        }
                                    }
                                    else if (response.data.busPosDto?.stSeq!! > busstopdata.size - 6){
                                        stSeqInt = busstopdata.size-3
                                        centerOfScreen = binding.pullScrean.height/2
                                        if (stSeqInt != null) {
                                            layoutManager.scrollToPositionWithOffset(stSeqInt, centerOfScreen)
                                        } else {
                                            Log.e("NullPointerException", "busPos is null or stSeq is null")
                                        }
                                    }
                                    else{
                                        if (stSeqInt != null) {
                                            layoutManager.scrollToPositionWithOffset(stSeqInt, centerOfScreen)
                                        } else {
                                            Log.e("NullPointerException", "busPos is null or stSeq is null")
                                        }
                                    }
                                    var type = ""
                                    if (response.data.busRouteName == "140"){
                                        if (response.data.busPosDto.stSeq < 44){
                                            if ((busstopdata[response.data.busPosDto.stSeq-1].reserved_cnt != 0) or (busstopdata[response.data.busPosDto.stSeq-1].drop_cnt != 0)){
                                                if (busstopdata[response.data.busPosDto.stSeq-1].wheelchair == true){
                                                    if (busstopdata[response.data.busPosDto.stSeq-1].blind == true){
                                                        type = "Blind person | Wheelchair"
                                                    }
                                                    else type = "Wheelchair"
                                                }
                                                else{
                                                    if (busstopdata[response.data.busPosDto.stSeq-1].blind == true){
                                                        type = "Blind person"
                                                    }
                                                }
                                                dialog(busstopdata[response.data.busPosDto.stSeq-1].reserved_cnt,busstopdata[response.data.busPosDto.stSeq-1].drop_cnt,type)
                                            }
                                        }
                                        else if(response.data.busPosDto.stSeq > 44){
                                            if ((busstopdata[response.data.busPosDto.stSeq-2].reserved_cnt != 0) or (busstopdata[response.data.busPosDto.stSeq-2].drop_cnt != 0)){
                                                if (busstopdata[response.data.busPosDto.stSeq-2].wheelchair == true){
                                                    if (busstopdata[response.data.busPosDto.stSeq-2].blind == true){
                                                        type = "Blind person | Wheelchair"
                                                    }
                                                    else type = "Wheelchair"
                                                }
                                                else{
                                                    if (busstopdata[response.data.busPosDto.stSeq-2].blind == true){
                                                        type = "Blind person"
                                                    }
                                                }
                                                dialog(busstopdata[response.data.busPosDto.stSeq-2].reserved_cnt,busstopdata[response.data.busPosDto.stSeq-2].drop_cnt,type)
                                            }
                                        }
                                    }
                                    else{
                                        if (response.data.busPosDto.stSeq < 44){
                                            if ((busstopdata[response.data.busPosDto.stSeq-1].reserved_cnt != 0) or (busstopdata[response.data.busPosDto.stSeq-1].drop_cnt != 0)){
                                                if (busstopdata[response.data.busPosDto.stSeq-1].wheelchair == true){
                                                    if (busstopdata[response.data.busPosDto.stSeq-1].blind == true){
                                                        type = "Blind person | Wheelchair"
                                                    }
                                                    else type = "Wheelchair"
                                                }
                                                else{
                                                    if (busstopdata[response.data.busPosDto.stSeq-1].blind == true){
                                                        type = "Blind person"
                                                    }
                                                }
                                                dialog(busstopdata[response.data.busPosDto.stSeq-1].reserved_cnt,busstopdata[response.data.busPosDto.stSeq-1].drop_cnt,type)
                                            }
                                        }
                                    }

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
    @SuppressLint("MissingInflatedId")
    fun dialog(boarding:Int, out:Int, type:String){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.popup,null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        val boarding_num = mDialogView.findViewById<TextView>(R.id.textView7)
        val out_num = mDialogView.findViewById<TextView>(R.id.textView9)
        val type_txt = mDialogView.findViewById<TextView>(R.id.next_type_txt)
        val nodialog = mDialogView.findViewById<ImageView>(R.id.imageView9)
        val title = mDialogView.findViewById<TextView>(R.id.popup_title)

        title.text = "Next Stop Information"
        title.visibility = View.VISIBLE

        boarding_num.text = boarding.toString()
        out_num.text = out.toString()
        type_txt.text = type
        type_txt.visibility = View.VISIBLE

        nodialog.setOnClickListener {
            mAlertDialog.dismiss()
        }
        // 타이머 설정
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                // 팝업을 UI 쓰레드에서 다루기 위해 runOnUiThread 사용
                runOnUiThread {
                    mAlertDialog.dismiss()
                }
                timer.cancel() // 타이머 종료
            }
        }, 5000) // 30초 후에 타이머 실행
    }
}