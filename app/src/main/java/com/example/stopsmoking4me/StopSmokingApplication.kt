package com.example.stopsmoking4me

import android.app.Application
import com.example.stopsmoking4me.prefs.MyPreferences

class StopSmokingApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MyPreferences.init(applicationContext)
    }
}