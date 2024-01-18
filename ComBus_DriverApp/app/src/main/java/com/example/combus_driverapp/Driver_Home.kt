package com.example.combus_driverapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.combus_driverapp.databinding.ActivityDriverHomeBinding

class Driver_Home : AppCompatActivity() {
    private lateinit var binding: ActivityDriverHomeBinding
    private val busstoplist = listOf(
        busstop("성신여대입구역3번출구","08457"),
        busstop("돈암시장입구","08305"),
        busstop("성신여대정문앞","08489"),
        busstop("성신여대평생교육원","08504"),
        busstop("성신여대후문","08311"),
        busstop("용문중고교","08313"),
        busstop("안암초교","08315"),
        busstop("고려대이공대","08564"),
        busstop("고대병원","08581")
    )
    private val bookpersonlist = listOf(
        busstop_book_person("성신여대입구역3번출구","chair"),
        busstop_book_person("성신여대입구역3번출구","unlook"),
        busstop_book_person("성신여대후문","unlook"),
        busstop_book_person("고대병원","unlook"),
        busstop_book_person("고대병원","unlook")
    )
    private val alightpersonlist = listOf(
        busstop_alight_person("성신여대입구역3번출구","unlook"),
        busstop_alight_person("고대병원","chair")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDriverHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeViews()

    }
    private fun initializeViews(){
        binding.bussropRecycle.layoutManager = LinearLayoutManager(this)
        binding.bussropRecycle.adapter = busstop_list_adapter(busstoplist)

    }
}