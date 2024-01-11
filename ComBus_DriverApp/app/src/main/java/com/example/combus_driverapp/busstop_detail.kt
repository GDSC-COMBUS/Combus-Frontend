package com.example.combus_driverapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.combus_driverapp.databinding.ActivityBusstopDetailBinding

class busstop_detail : AppCompatActivity() {
    private lateinit var binding: ActivityBusstopDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityBusstopDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}