package com.app.domain.usecase.dashboard

import com.app.domain.entity.dashboard.StockAlert
import com.app.domain.repository.DashboardRepository

class GetLowStockAlertsUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): List<StockAlert> = repository.getLowStockAlerts()
}