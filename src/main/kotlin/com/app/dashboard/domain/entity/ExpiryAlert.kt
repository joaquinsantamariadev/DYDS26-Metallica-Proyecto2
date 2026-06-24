package com.app.dashboard.domain.entity

import java.time.LocalDate

data class ExpiryAlert(
    val productId: Long,
    val productName: String,
    val expiryDate: LocalDate,
    val daysRemaining: Int
)