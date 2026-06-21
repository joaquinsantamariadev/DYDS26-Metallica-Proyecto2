package com.app.presentation.dashboard

import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.entity.dashboard.StockAlert
import com.app.domain.repository.DashboardRepository

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