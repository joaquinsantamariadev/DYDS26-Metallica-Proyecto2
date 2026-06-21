package com.app.domain.repository

import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.entity.dashboard.StockAlert

interface DashboardRepository {
    suspend fun getMetrics(): DashboardMetrics
    suspend fun getLowStockAlerts(): List<StockAlert>
    suspend fun getExpiryAlerts(withinDays: Int): List<ExpiryAlert>
    suspend fun getRecentSales(limit: Int): List<RecentSaleEntry>
}