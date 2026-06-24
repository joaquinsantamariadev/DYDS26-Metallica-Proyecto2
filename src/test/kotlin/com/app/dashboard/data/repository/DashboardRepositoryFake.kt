package com.app.dashboard.data.repository

import com.app.dashboard.domain.entity.DashboardMetrics
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.entity.StockAlert
import com.app.dashboard.domain.repository.DashboardRepository

class DashboardRepositoryFake : DashboardRepository {
    var metricsResult: DashboardMetrics = DashboardMetrics(0, 0.0, 0, 0.0, 0, 0, false)
    var stockAlertsResult: List<StockAlert> = emptyList()
    var expiryAlertsResult: List<ExpiryAlert> = emptyList()
    var recentSalesResult: List<RecentSaleEntry> = emptyList()
    var shouldThrowError = false
    var capturedWithinDays: Int? = null
    var capturedLimit: Int? = null

    override suspend fun getMetrics(): DashboardMetrics {
        if (shouldThrowError) throw Exception("metrics error")
        return metricsResult
    }

    override suspend fun getLowStockAlerts(): List<StockAlert> {
        if (shouldThrowError) throw Exception("stock error")
        return stockAlertsResult
    }

    override suspend fun getExpiryAlerts(withinDays: Int): List<ExpiryAlert> {
        if (shouldThrowError) throw Exception("expiry error")
        capturedWithinDays = withinDays
        return expiryAlertsResult
    }

    override suspend fun getRecentSales(limit: Int): List<RecentSaleEntry> {
        if (shouldThrowError) throw Exception("sales error")
        capturedLimit = limit
        return recentSalesResult
    }
}