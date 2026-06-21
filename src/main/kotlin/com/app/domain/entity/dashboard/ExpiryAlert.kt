package com.app.domain.entity.dashboard

import java.time.LocalDate

data class ExpiryAlert(
    val productId: Long,
    val productName: String,
    val expiryDate: LocalDate,
    val daysRemaining: Int
)