package org.techtown.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.techtown.myapplication.databinding.ActivityMainBinding
import org.techtown.myapplication.databinding.ActivityNoReservationBinding

class NoReservation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_no_reservation)

        super.onCreate(savedInstanceState)
        val binding = ActivityNoReservationBinding.inflate(layoutInflater);
        setContentView(binding.root)

        binding.buttonStartReservation.setOnClickListener {
            val intent = Intent(this, BoardingBusStop::class.java) //다음화면으로 이동하기 위한 인텐트 객체 생성
            startActivity(intent)
        }

    }
}