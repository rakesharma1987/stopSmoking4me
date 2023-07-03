package com.example.stopsmoking4me.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Utility {
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}