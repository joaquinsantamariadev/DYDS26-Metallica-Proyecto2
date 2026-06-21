package com.app.domain.entity.dashboard

data class DashboardMetrics(
    val totalProducts: Int,
    val inventoryValue: Double,
    val salesToday: Int,
    val revenueToday: Double,
    val lowStockCount: Int,
    val nearExpiryCount: Int,
    val hasActiveSession: Boolean
)