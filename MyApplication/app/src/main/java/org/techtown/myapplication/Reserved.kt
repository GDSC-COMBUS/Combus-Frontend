package org.techtown.myapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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
            updateStatusOnServer(reservationData?.id, "승차 완료") { response ->
                handleApiResponse(response)
            }
        }

        // "하차 완료" 버튼 클릭 시
        binding.buttonStatus2.setOnClickListener {
            updateStatusOnServer(reservationData?.id, "하차 완료") { response ->
                handleApiResponse(response)
            }
        }
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