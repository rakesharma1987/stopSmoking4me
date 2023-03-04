package com.example.stopsmoking4me.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stopsmoking4me.util.DateTimeConverters
import java.util.Date

@Entity
data class Reason(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String = "",
    var name: String = "",
    var forWhom: String = "",
    var whomName: String = "",
    var dropDownReason: String = "",
    var yesOrNo: Boolean = false,
    var date: Date = Date(),
)
