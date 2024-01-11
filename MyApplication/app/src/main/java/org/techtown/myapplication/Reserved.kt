package org.techtown.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.myapplication.databinding.ActivityMainBinding

class Reserved : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val menuList = arrayListOf(
            Reservation("일시", "2023.05.23"),
            Reservation("탑승 정류장", "돈암사거리.성신여대입구(중), 삼선교.한성대학교(중)방면"),
            Reservation("버스 번호", "102번"),
            Reservation("하차 정류장", "혜화동로터리.여운형활동터(중), 명륜3가.성대입구(중) 방면"),
            Reservation("승차 여부", "N")
        )

        //바인딩 오류
        //binding.reLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //RecyclerView.Adapter = ReservationAdapter()
    }
}