package org.techtown.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import org.techtown.myapplication.Retrofit.ApiManager_homeReservation
import org.techtown.myapplication.Retrofit.ApiManager_login
import org.techtown.myapplication.Retrofit.HomeReservationResponse
import org.techtown.myapplication.Retrofit.LoginRequest
import org.techtown.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Reserved::class.java)
            startActivity(intent)

//            val intent = Intent(this@MainActivity, NoReservation::class.java)
//            startActivity(intent)

            // 사용 예시
            val userService = ApiManager_login.create().loginUser(LoginRequest("1234"))
            val response = userService.execute()

            if (response.isSuccessful) {
                val loginResponse = response.body()
                if (loginResponse?.status == "OK") {
                    // 성공적으로 로그인한 경우
                    val userData = loginResponse.data
                    // userData.id, userData.name, userData.loginId 등으로 사용자 정보에 접근할 수 있습니다.

                    // API 호출 예시
                    val homeReservationService = ApiManager_homeReservation.create().getHomeReservation()
                    val call = homeReservationService.enqueue(object :
                        Callback<HomeReservationResponse> {
                        override fun onResponse(
                            call: Call<HomeReservationResponse>,
                            response: Response<HomeReservationResponse>
                        ) {
                            if (response.isSuccessful) {
                                val homeReservationResponse = response.body()
                                if (homeReservationResponse?.status == "OK") {
                                    // 홈 예약 내역이 존재하는 경우
                                    val reservationData = homeReservationResponse.data
                                    // reservationData.id, reservationData.boardingStop 등으로 예약 정보에 접근할 수 있습니다.
                                    Log.d("MainActivity", "Reservation exists: ${reservationData?.boardingStop}")
                                    // 다른 화면으로 전환
                                    // Intent를 통해 예약 정보를 ReservedActivity로 전달
                                    val intent = Intent(this@MainActivity, Reserved::class.java)
                                    intent.putExtra("reservationData", reservationData) // 다른 화면으로 데이터 전달 예시
                                    startActivity(intent)
                                    finish() // 현재 화면 종료

                                } else {
                                    // 홈 예약 내역이 존재하지 않는 경우
                                    Log.d("MainActivity", "No reservation exists")
                                    // 다른 화면으로 전환
                                    val intent = Intent(this@MainActivity, NoReservation::class.java)
                                    //intent.putExtra("userId", userData.id) // 다른 화면으로 데이터 전달 예시
                                    startActivity(intent)
                                    finish() // 현재 화면 종료
                                }
                            } else {
                                // 네트워크 오류 또는 서버 응답 오류
                                Log.d("MainActivity", "Network error or server response error: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<HomeReservationResponse>, t: Throwable) {
                            // 통신 실패 시
                            Log.d("MainActivity", "Call failed: ${t.message}")
                        }
                    })

                    // 다른 화면으로 전환
                    val intent = Intent(this@MainActivity, Reserved::class.java)
                    //intent.putExtra("userId", userData.id) // 다른 화면으로 데이터 전달 예시
                    startActivity(intent)
                    finish() // 현재 화면 종료
                } else {
                    // 로그인 실패한 경우
                    val detail = loginResponse?.detail ?: "Unknown error"
                    println("Login failed: $detail")
                }
            } else {
                // 네트워크 오류 또는 서버 응답 오류
                println("Network error or server response error: ${response.code()}")
            }
        }
    }
}

