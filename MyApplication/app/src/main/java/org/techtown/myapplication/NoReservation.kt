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

        // 버튼 클릭 시 화면 전환(승차 정류장 선택 및 예약하기 화면으로)
        binding.buttonStartReservation.setOnClickListener {
            // Intent를 사용하여 화면 전환
            val intent = Intent(this, BoardingBusStop::class.java)
            startActivity(intent)
        }
    }
}