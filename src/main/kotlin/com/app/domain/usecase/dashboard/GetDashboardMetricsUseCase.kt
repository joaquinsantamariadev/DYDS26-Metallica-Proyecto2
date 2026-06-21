package com.app.domain.usecase.dashboard

import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.repository.DashboardRepository

class GetDashboardMetricsUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): DashboardMetrics = repository.getMetrics()
}