package com.example.combus_driverapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class MyApplication : Application() {
    companion object{
        lateinit var user: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        user = getSharedPreferences("user", Context.MODE_PRIVATE)
    }
}