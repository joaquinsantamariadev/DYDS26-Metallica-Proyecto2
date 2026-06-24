package com.app.dashboard.domain.usecase

import com.app.dashboard.domain.entity.DashboardMetrics
import com.app.dashboard.domain.repository.DashboardRepository

interface GetDashboardMetricsUseCase {
    suspend operator fun invoke(): DashboardMetrics
}

class GetDashboardMetricsUseCaseImpl(private val repository: DashboardRepository) : GetDashboardMetricsUseCase {
    override suspend operator fun invoke(): DashboardMetrics = repository.getMetrics()
}