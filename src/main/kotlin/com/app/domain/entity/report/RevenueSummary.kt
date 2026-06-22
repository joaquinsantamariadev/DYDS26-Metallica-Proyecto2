package com.app.domain.entity.report

data class RevenueSummary(
    val totalRevenue: Double,
    val totalSales: Int,
    val averageTicket: Double,
    val dataPoints: List<RevenueDataPoint>
)