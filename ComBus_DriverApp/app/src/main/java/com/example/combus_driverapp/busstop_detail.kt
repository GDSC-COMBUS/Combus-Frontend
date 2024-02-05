package com.example.combus_driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.combus_driverapp.databinding.ActivityBusstopDetailBinding

class busstop_detail : AppCompatActivity() {
    private lateinit var binding: ActivityBusstopDetailBinding
    val extras = intent.extras
    val arsId = extras!!["arsId"] as Int
    private val booklist = listOf(
        booking("wait","시각장애인","성신여대입구","고속터미널"),
        booking("wait","휠체어","성신여대입구","고속터미널"),
        booking("wait","휠체어","성신여대입구","고속터미널")
    )
    private val alightlist = listOf(
        alight("ing","시각장애인","고속터미널","성신여대입구"),
        alight("ing","시각장애인","고속터미널","성신여대입구"),
        alight("ing","휠체어","고속터미널","성신여대입구")
    )
    var book_chair_num = 0
    var book_unlook_num = 0
    var alight_chair_num = 0
    var alight_unlook_num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBusstopDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val extras = intent.extras
        val busstop_name = extras!!["busstop_name"].toString()
        val busstop_num = extras!!["busstop_num"].toString()

        for(i:Int in 1..booklist.size){
            if(booklist[i-1].type=="시각장애인") {
                book_unlook_num++
            }
            else if (booklist[i-1].type == "휠체어"){
                book_chair_num++
            }
        }
        for(i:Int in 1..alightlist.size){
            if(alightlist[i-1].type=="시각장애인") {
                alight_unlook_num++
            }
            else if (alightlist[i-1].type == "휠체어"){
                alight_chair_num++
            }
        }
        binding.txtBusstopName.text = busstop_name
        binding.txtBusstopNum.text = busstop_num

        if (book_chair_num == 0){
            if (book_unlook_num == 0) binding.txtBusstopBookInforNum.text = ""
            else binding.txtBusstopBookInforNum.text = "시각장애인 ${book_unlook_num}영"
        }
        else {
            if (book_unlook_num == 0) binding.txtBusstopBookInforNum.text = "휠체어 ${book_chair_num}영"
            else binding.txtBusstopBookInforNum.text = "휠체어 ${book_chair_num}영 시각장애인 ${book_unlook_num}영"
        }
        if (alight_chair_num == 0){
            if (alight_unlook_num == 0) binding.txtBusstopOutInforNum.text = ""
            else binding.txtBusstopOutInforNum.text = "시각장애인 ${alight_unlook_num}영"
        }
        else {
            if (alight_unlook_num == 0) binding.txtBusstopOutInforNum.text = "휠체어 ${alight_chair_num}영"
            else binding.txtBusstopOutInforNum.text = "휠체어 ${alight_chair_num}영 시각장애인 ${alight_unlook_num}영"
        }

        binding.txtBusstopBookNum.text = "예약 수 ${book_chair_num+book_unlook_num}명"
        binding.txtBusstopOutNum.text = "하차 수 ${alight_chair_num+alight_unlook_num}명"

        initializeViews()
    }
    private fun initializeViews(){
        //val LinearLayoutManager1 = LinearLayoutManager(this)
        binding.busstopBookRecycle.layoutManager = LinearLayoutManager(this)
        binding.busstopBookRecycle.adapter = busstop_detail_book_adapter(booklist)
        binding.busstopOutRecycle.layoutManager = LinearLayoutManager(this)
        binding.busstopOutRecycle.adapter = busstop_detail_alight_adapter(alightlist)
    }
}