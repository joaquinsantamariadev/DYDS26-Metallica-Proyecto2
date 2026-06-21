package com.app.presentation.dashboard

import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.entity.dashboard.StockAlert
import com.app.domain.entity.dashboard.DashboardMetrics

data class DashboardUiState(
    val metrics: DashboardMetrics? = null,
    val stockAlerts: List<StockAlert> = emptyList(),
    val expiryAlerts: List<ExpiryAlert> = emptyList(),
    val recentSales: List<RecentSaleEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)