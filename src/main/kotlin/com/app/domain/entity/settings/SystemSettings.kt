package com.app.domain.entity.settings

data class SystemSettings(
    val defaultLowStockThreshold: Int = 5,
    val expiryAlertDays: Int = 7,
    val historyPageSize: Int = 50,
    val rotationTopN: Int = 20
)
