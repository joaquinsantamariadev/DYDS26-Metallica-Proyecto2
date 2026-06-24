package com.app.dashboard.domain.entity

import java.time.LocalDateTime

data class RecentSaleEntry(
    val saleId: Long,
    val dateTime: LocalDateTime,
    val itemCount: Int,
    val total: Double
)