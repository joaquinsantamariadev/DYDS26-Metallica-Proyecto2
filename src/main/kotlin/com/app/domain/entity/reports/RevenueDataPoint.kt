package com.app.domain.entity.reports

data class RevenueDataPoint(
    val periodLabel: String,
    val revenue: Double,
    val salesCount: Int
)