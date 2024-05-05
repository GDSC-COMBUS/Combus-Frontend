package org.techtown.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.techtown.myapplication.Retrofit.ApiManager_homeReservation
import org.techtown.myapplication.Retrofit.HomeReservationResponse
import org.techtown.myapplication.Retrofit.ReservationData
import org.techtown.myapplication.databinding.ActivityReservedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Reserved : AppCompatActivity() {
    private var reservationData: ReservationData? = null
    private lateinit var binding: ActivityReservedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 예약 정보를 가져옵니다
        reservationData = intent.getParcelableExtra("reservationData")

        if (reservationData != null) {
            binding.createdAt.text = reservationData?.date
            binding.boardingStop.text = reservationData?.boardingStop
            binding.busId.text = reservationData?.busRouteName
            binding.dropStop.text = reservationData?.dropStop
            if ( reservationData?.status == "승차 전: 버스 오는 중")
                binding.status.text = "Before boarding: Bus on the way"
            if (reservationData?.status == "탑승 완료: 목적지로 가는 중") {
                binding.buttonStatus.backgroundTintList = getColorStateList(R.color.clickedTextColor)
                binding.buttonStatus.isEnabled = false
                binding.status.text = "Boarding complete: en route to destination"
            }
        }

        // "승차 완료" 버튼 클릭 시
        binding.buttonStatus.setOnClickListener {
            showConfirmationDialog("Are you done getting on?") {
                // 사용자가 확인을 누른 경우에만 아래 코드 실행
                updateStatusOnServer(reservationData?.id, "승차 완료") { response ->
                    handleApiResponse(response)
                    if (response != null) {
                        // 서버 응답을 받았을 때만 버튼을 비활성화하고 색상을 변경
                        binding.buttonStatus.backgroundTintList = getColorStateList(R.color.clickedTextColor)
                        binding.buttonStatus.isEnabled = false
                    }
                }
            }
        }

// "하차 완료" 버튼 클릭 시
        binding.buttonStatus2.setOnClickListener {
            showConfirmationDialog("Are you done getting off?") {
                // 사용자가 확인을 누른 경우에만 아래 코드 실행
                updateStatusOnServer(reservationData?.id, "하차 완료") { response ->
                    handleApiResponse(response)
                    if (response != null) {
                        // 서버 응답을 받았을 때만 버튼을 비활성화하고 색상을 변경
                        binding.buttonStatus2.backgroundTintList = getColorStateList(R.color.clickedTextColor)
                        binding.buttonStatus2.isEnabled = false
                        val intent = Intent(this, NoReservation::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
        //Toast.makeText(this, reservationData!!.busRouteName.toString(),Toast.LENGTH_SHORT).show()
        binding.buttonCamera.setOnClickListener {
            val intent = Intent(this,Camera_page2::class.java)
            intent.putExtra("bus_num", reservationData?.busRouteName.toString())
            startActivity(intent)
        }
    }

    // 확인 대화 상자를 표시하는 함수
    private fun showConfirmationDialog(message: String, onConfirmed: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                onConfirmed.invoke()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // 서버에 상태 업데이트 요청을 보내는 함수
    private fun updateStatusOnServer(reservationId: Long?, newStatus: String, callback: (HomeReservationResponse?) -> Unit) {
        val call = ApiManager_homeReservation.updateReservationStatus(reservationId)

        call.enqueue(object : Callback<HomeReservationResponse> {
            override fun onResponse(call: Call<HomeReservationResponse>, response: Response<HomeReservationResponse>) {
                if (response.isSuccessful) {
                    // 서버 응답이 성공한 경우
                    callback(response.body())
                } else {
                    // 서버 응답이 실패한 경우
                    Log.e("ddddddd", "Unsuccessful response: ${response.code()}")
                    showToast("서버 응답 오류가 발생했습니다. (${response.code()})")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<HomeReservationResponse>, t: Throwable) {
                // 네트워크 오류 발생
                Log.e("ddddddd", "Network error", t)
                showToast("네트워크 오류가 발생했습니다.")
                callback(null)
            }
        })
    }

    // 서버 응답을 처리하는 함수
    private fun handleApiResponse(response: HomeReservationResponse?) {
        if (response != null) {
            // 예약 상태 업데이트 및 토스트 메시지 처리 등 추가 작업 가능
            binding.status.text = "Boarding complete: en route to destination"
        } else {
            showToast("네트워크 오류 또는 서버 응답 오류가 발생했습니다.")
        }
    }

    // showToast 함수는 사용자에게 메시지를 보여주는 함수입니다.
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}