package com.bpapps.childprotector.model.classes

data class AppUsageStatInterval(
    val _id: String,
    val _userIs: String,
    val _appName: String,
    val _appCreator: String,
    val _appUsageTime: Double,
    val _interval: Int
)