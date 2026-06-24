package com.app.dashboard.domain.repository

import com.app.dashboard.domain.entity.DashboardMetrics
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.entity.StockAlert

interface DashboardRepository {
    suspend fun getMetrics(): DashboardMetrics
    suspend fun getLowStockAlerts(): List<StockAlert>
    suspend fun getExpiryAlerts(withinDays: Int): List<ExpiryAlert>
    suspend fun getRecentSales(limit: Int): List<RecentSaleEntry>
}