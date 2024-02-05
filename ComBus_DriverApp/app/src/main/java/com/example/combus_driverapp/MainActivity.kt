package com.example.combus_driverapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.combus_driverapp.connection.RetrofitClient
import com.example.combus_driverapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val user = MyApplication.user
    private val editor = user.edit()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val id = binding.driverNum.text.toString()

            val call = RetrofitObject.getRetrofitService.login(RetrofitClient.requestlogin(id))
            call.enqueue(object : Callback<RetrofitClient.responselogin>{
                override fun onResponse(
                    call: Call<RetrofitClient.responselogin>,
                    response: Response<RetrofitClient.responselogin>
                ) {
                    if (response.isSuccessful){
                        val response = response.body()
                        if (response != null){
                            if (response.status == "OK"){
                                val sessionId = response.data.sessionId
                                editor.putString("id",id)
                                editor.putString("sessionid",sessionId)
                                editor.apply()
                                val intent = Intent(this@MainActivity,Driver_Home::class.java)
                                intent.putExtra("driverId",response.data.driverId)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@MainActivity,response.detail,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RetrofitClient.responselogin>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })

            val showintent = Intent(this,Driver_Home::class.java)
            startActivity(showintent)
        }

    }
}