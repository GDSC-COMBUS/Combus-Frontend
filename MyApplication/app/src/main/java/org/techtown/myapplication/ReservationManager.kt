package org.techtown.myapplication

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//ReservationManager 클래스 작성
class ReservationManager {
    private val homeService: HomeService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("BASE_URL_HERE")  // 여기에 서버의 base URL을 넣어주세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        homeService = retrofit.create(HomeService::class.java)
    }

    fun getReservationStatus() {
        homeService.getReservationStatus().enqueue(object : Callback<ReservationResponse> {
            override fun onResponse(
                call: Call<ReservationResponse>,
                response: Response<ReservationResponse>
            ) {
                if (response.isSuccessful) {
                    val reservationResponse = response.body()

                    if (reservationResponse?.data != null) {
                        // 예약 내역이 존재하는 경우 처리
                        val reservationData = reservationResponse.data
                        // 예약 내역을 사용하여 화면에 표시하거나 다른 작업 수행
                    } else {
                        // 예약 내역이 존재하지 않는 경우 처리
                    }
                } else {
                    // 응답 코드가 404 등 에러일 때 처리
                    // response.errorBody()를 통해 에러 상세 정보에 접근 가능
                }
            }

            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                // 네트워크 통신 실패 시 처리
            }
        })
    }
}
