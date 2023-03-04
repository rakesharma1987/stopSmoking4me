package com.example.stopsmoking4me.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Quotes(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var quotes: String = "",
    var author: String = ""
)
