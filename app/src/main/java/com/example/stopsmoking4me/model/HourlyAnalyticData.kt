package com.example.stopsmoking4me.model

data class HourlyAnalyticData(
    var sno: Int,
    var hour: String,
    var reason: String,
    var count: Int,
    var percentage: String
)
