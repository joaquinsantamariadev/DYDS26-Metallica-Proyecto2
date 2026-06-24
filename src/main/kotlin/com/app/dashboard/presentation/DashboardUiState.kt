package com.app.dashboard.presentation

import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.entity.StockAlert
import com.app.dashboard.domain.entity.DashboardMetrics

data class DashboardUiState(
    val metrics: DashboardMetrics? = null,
    val stockAlerts: List<StockAlert> = emptyList(),
    val expiryAlerts: List<ExpiryAlert> = emptyList(),
    val recentSales: List<RecentSaleEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val exchangeRate: ExchangeRate? = null,
    val exchangeRateUnavailable: Boolean = false
)