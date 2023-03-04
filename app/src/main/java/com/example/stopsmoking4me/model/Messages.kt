package com.example.stopsmoking4me.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Messages(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var msg: String = "",
    var colorType: String = ""
)
