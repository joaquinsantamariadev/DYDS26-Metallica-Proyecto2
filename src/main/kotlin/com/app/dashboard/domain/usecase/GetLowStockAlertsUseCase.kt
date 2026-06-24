package com.app.dashboard.domain.usecase

import com.app.dashboard.domain.entity.StockAlert
import com.app.dashboard.domain.repository.DashboardRepository

interface GetLowStockAlertsUseCase {
    suspend operator fun invoke(): List<StockAlert>
}

class GetLowStockAlertsUseCaseImpl(private val repository: DashboardRepository) : GetLowStockAlertsUseCase {
    override suspend operator fun invoke(): List<StockAlert> = repository.getLowStockAlerts()
}