package org.techtown.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import org.techtown.myapplication.databinding.ActivityMainBinding
import org.techtown.myapplication.databinding.ActivityNoReservationBinding

class NoReservation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNoReservationBinding.inflate(layoutInflater);
        setContentView(binding.root)

        // Intent를 통해 전달된 데이터 받기
        val userId = intent.getLongExtra("userId", -1L) // 기본값으로 -1L을 설정하여 오류 방지

        // 버튼 클릭 시 화면 전환(승차 정류장 선택 및 예약하기 화면으로)
        binding.buttonStartReservation.setOnClickListener {
            // Intent를 사용하여 화면 전환
            val boardingBusStopintent = Intent(this, BoardingBusStop::class.java)
            boardingBusStopintent.putExtra("userId", userId)
            startActivity(boardingBusStopintent)
            Log.d("NoReservation", "Button clicked. Starting new activity.")
        }
    }
}