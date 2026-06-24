package com.app.reports.domain.entity

data class RevenueSummary(
    val totalRevenue: Double,
    val totalSales: Int,
    val averageTicket: Double,
    val dataPoints: List<RevenueDataPoint>
)