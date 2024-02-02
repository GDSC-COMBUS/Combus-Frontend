package org.techtown.myapplication

import android.os.Bundle
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
            binding.createdAt.text = reservationData?.createdAt
            binding.boardingStop.text = reservationData?.boardingStop
            binding.busId.text = reservationData?.busId.toString()
            binding.dropStop.text = reservationData?.dropStop.toString()
            binding.status.text = reservationData?.status
        }

        // "승차 완료" 버튼 클릭 시
        binding.buttonStatus.setOnClickListener {
            showConfirmationDialog("승차 완료하셨습니까?") {
                // 사용자가 확인을 누른 경우에만 아래 코드 실행
                binding.buttonStatus.setTextColor(resources.getColor(R.color.clickedTextColor))
                updateStatusOnServer(reservationData?.id, "승차 완료") { response ->
                    handleApiResponse(response)
                }
                binding.buttonStatus.isEnabled = false
            }
        }

        // "하차 완료" 버튼 클릭 시
        binding.buttonStatus2.setOnClickListener {
            showConfirmationDialog("하차 완료하셨습니까?") {
                // 사용자가 확인을 누른 경우에만 아래 코드 실행
                binding.buttonStatus.setTextColor(resources.getColor(R.color.clickedTextColor))
                updateStatusOnServer(reservationData?.id, "하차 완료") { response ->
                    handleApiResponse(response)
                }
                binding.buttonStatus.isEnabled = false
            }
        }
    }

    // 확인 대화 상자를 표시하는 함수
    private fun showConfirmationDialog(message: String, onConfirmed: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("예") { _, _ ->
                onConfirmed.invoke()
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    // 서버에 상태 업데이트 요청을 보내는 함수
    private fun updateStatusOnServer(reservationId: Long?, newStatus: String, callback: (HomeReservationResponse?) -> Unit) {
        val call = ApiManager_homeReservation.updateReservationStatus(reservationId, newStatus)

        call.enqueue(object : Callback<HomeReservationResponse> {
            override fun onResponse(call: Call<HomeReservationResponse>, response: Response<HomeReservationResponse>) {
                callback(response.body())
            }

            override fun onFailure(call: Call<HomeReservationResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    // 서버 응답을 처리하는 함수
    private fun handleApiResponse(response: HomeReservationResponse?) {
        if (response != null) {
            showToast(response.detail)
            // 예약 상태 업데이트 및 토스트 메시지 처리 등 추가 작업 가능
        } else {
            showToast("네트워크 오류 또는 서버 응답 오류가 발생했습니다.")
        }
    }

    // showToast 함수는 사용자에게 메시지를 보여주는 함수입니다.
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}