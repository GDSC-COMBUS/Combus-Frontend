package com.example.combus_driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.ActivityBusstopDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class busstop_detail : AppCompatActivity() {
    private lateinit var binding: ActivityBusstopDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBusstopDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val extras = intent.extras
        val busstop_name = extras!!.getString("busstop_name")
        val busstop_num = extras!!.getLong("busstop_num")
        val boarding_num = extras!!.getInt("bording_num")
        val drop_num = extras!!.getInt("drop_num")
        val arsId = extras!!.getLong("arsId")

        binding.txtBusstopName.text = busstop_name
        binding.txtBusstopNum.text = busstop_num.toString()

        binding.txtBusstopBookNum.visibility = View.VISIBLE
        binding.txtBusstopBookNum.text = "Reservaion ${boarding_num}"

        binding.txtBusstopOutNum.visibility = View.VISIBLE
        binding.txtBusstopOutNum.text = "Dropping off ${drop_num}"
        /*if (boarding_num>0){

        }
        //else binding.txtBusstopBookNum.visibility = View.GONE

        if (drop_num>0){

        }*/
        //else binding.txtBusstopOutNum.visibility = View.GONE

        val retrofitService = RetrofitObject.getRetrofitService
        val call = retrofitService.busstopDetail(arsId)

        call.enqueue(object : Callback<RetrofitClient.responsebusstopDetail> {
            override fun onResponse(call: Call<RetrofitClient.responsebusstopDetail>, response: Response<RetrofitClient.responsebusstopDetail>) {
                if (response.isSuccessful) {
                    // 서버로부터 응답을 성공적으로 받았을 때 처리
                    val responseData = response.body()
                    responseData?.let {
                        // 데이터를 사용하여 UI 업데이트 또는 처리 작업 수행
                        val detailboardingdata = responseData.data.boardingInfo
                        val detaildropdata= responseData.data.dropInfo
                        Log.d("data",detailboardingdata.toString())
                        Log.d("data",detaildropdata.toString())

                        binding.txtBusstopBookInforNum.text = "Blind person ${responseData.data.boardingBlindCnt} | Wheelchair ${responseData.data.boardingWheelchairCnt}"
                        binding.txtBusstopOutInforNum.text = "Blind person ${responseData.data.dropBlindCnt} | Wheelchair ${responseData.data.dropWheelchairCnt}"

                        binding.busstopBookRecycle.adapter = busstop_detail_book_adapter(detailboardingdata)
                        binding.busstopOutRecycle.adapter = busstop_detail_alight_adapter(detaildropdata)

                        initializeViews()
                    }
                } else {
                    // 서버로부터 응답을 받지 못했을 때 처리
                    Log.d("Retrofit", "false")
                }
            }

            override fun onFailure(call: Call<RetrofitClient.responsebusstopDetail>, t: Throwable) {
                // 네트워크 오류 등의 이유로 서버에 요청을 보내지 못했을 때 처리
                val errorMessage = "Call Failed: ${t.message} "
                Log.d("Retrofit", errorMessage)
            }
        })


    }
    private fun initializeViews(){
        //val LinearLayoutManager1 = LinearLayoutManager(this)
        binding.busstopBookRecycle.layoutManager = LinearLayoutManager(this)

        binding.busstopOutRecycle.layoutManager = LinearLayoutManager(this)

    }
}