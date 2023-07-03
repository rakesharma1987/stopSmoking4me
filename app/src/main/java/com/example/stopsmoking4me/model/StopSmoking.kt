package com.example.stopsmoking4me.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StopSmoking(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var reason: String = "",
    var isSmoking: Boolean = false,
    var dateString: String = "",
    var hour: String = "",
    var day: String = "",
)
