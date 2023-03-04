package com.example.stopsmoking4me.util

import androidx.room.TypeConverter
import java.util.*

class DateTimeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time
    }
}