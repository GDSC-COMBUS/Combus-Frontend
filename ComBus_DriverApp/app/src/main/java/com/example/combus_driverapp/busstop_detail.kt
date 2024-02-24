package com.example.combus_driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
        val boarding_num = extras!!.getInt("boarding_num").toString()
        val drop_num = extras!!.getInt("drop_num").toString()
        val get_arsId = extras!!.getLong("arsId")
        val arsId = String.format("%05d", get_arsId)

        binding.txtBusstopName.text = busstop_name
        binding.txtBusstopNum.text = busstop_num.toString()

        binding.txtBusstopBookNum.visibility = View.VISIBLE
        binding.txtBusstopBookNum.text = "Reservaion ${boarding_num}"

        binding.txtBusstopOutNum.visibility = View.VISIBLE
        binding.txtBusstopOutNum.text = "Drop off ${drop_num}"

        val call = RetrofitObject.getRetrofitService.busstopDetail(arsId)
        call.enqueue(object : Callback<RetrofitClient.responsebusstopDetail>{
            override fun onResponse(
                call: Call<RetrofitClient.responsebusstopDetail>,
                response: Response<RetrofitClient.responsebusstopDetail>
            ) {
                if (response.isSuccessful){
                    Log.d("response","ok")
                    val response = response.body()
                    if (response != null){
                        if (response.status == "OK"){
                            Log.d("data",arsId.toString())
                            Log.d("data",response.detail)
                            Log.d("data",response.data.boardingInfo.toString())
                            Log.d("data",response.data.dropInfo.toString())
                            Log.d("data",response.data.boardingBlindCnt.toString())
                            Log.d("data",response.data.dropBlindCnt.toString())
                            Log.d("data",response.data.boardingWheelchairCnt.toString())
                            Log.d("data",response.data.dropWheelchairCnt.toString())
                            val detailboardingdata = response.data.boardingInfo
                            val detaildropdata= response.data.dropInfo
                            Log.d("data",detailboardingdata.toString())
                            Log.d("data",detaildropdata.toString())
                            binding.txtBusstopBookInforNum.text = "Blind ${response.data.boardingBlindCnt} | Wheelchair ${response.data.boardingWheelchairCnt}"
                            binding.txtBusstopOutInforNum.text = "Blind ${response.data.dropBlindCnt} | Wheelchair ${response.data.dropWheelchairCnt}"

                            binding.busstopBookRecycle.adapter = busstop_detail_book_adapter(detailboardingdata)
                            binding.busstopOutRecycle.adapter = busstop_detail_alight_adapter(detaildropdata)
                            val layoutManagerBook = LinearLayoutManager(this@busstop_detail)
                            binding.busstopBookRecycle.layoutManager = layoutManagerBook

                            val layoutManagerOut = LinearLayoutManager(this@busstop_detail)
                            binding.busstopOutRecycle.layoutManager = layoutManagerOut

                        }else{
                            Toast.makeText(this@busstop_detail,response.detail, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    // 서버로부터 응답을 받지 못했을 때 처리
                    Log.d("Retrofit", "false")
                }
            }
            override fun onFailure(call: Call<RetrofitClient.responsebusstopDetail>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message} "
                Log.d("Retrofit", errorMessage)
            }
        })
    }
}