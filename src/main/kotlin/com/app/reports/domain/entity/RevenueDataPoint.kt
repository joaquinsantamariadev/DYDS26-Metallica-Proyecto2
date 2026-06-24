package com.app.reports.domain.entity

data class RevenueDataPoint(
    val periodLabel: String,
    val revenue: Double,
    val salesCount: Int
)