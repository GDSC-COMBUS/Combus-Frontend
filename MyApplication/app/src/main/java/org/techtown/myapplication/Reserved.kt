package org.techtown.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.techtown.myapplication.Retrofit.ReservationData
import org.techtown.myapplication.databinding.ActivityReservedBinding

class Reserved : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReservedBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        a = findViewById(R.id.btn_left);
//        b = findViewById(R.id.btn_center);
//        c = findViewById(R.id.btn_right);
//
//        btn_center.bringToFront();
//        btn_left.bringToFront();

        // Intent에서 예약 정보를 가져옵니다
        val reservationData = intent.getParcelableExtra<ReservationData>("reservationData")

        if (reservationData != null) {
            // 가져온 예약 정보를 TextView에 표시합니다
            findViewById<TextView>(R.id.createdAt).text = reservationData.createdAt
            findViewById<TextView>(R.id.createdAt).text = reservationData.boardingStop
            findViewById<TextView>(R.id.busId).text = reservationData.busId.toString()
            findViewById<TextView>(R.id.dropStop).text = reservationData.dropStop.toString()
            findViewById<TextView>(R.id.status).text = reservationData.status
        }
    }
}