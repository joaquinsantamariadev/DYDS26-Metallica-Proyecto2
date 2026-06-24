package com.app.settings.domain.entity

data class SystemSettings(
    val defaultLowStockThreshold: Int = 5,
    val expiryAlertDays: Int = 7,
    val historyPageSize: Int = 50,
    val rotationTopN: Int = 20,
    val isDarkMode: Boolean = false
)
