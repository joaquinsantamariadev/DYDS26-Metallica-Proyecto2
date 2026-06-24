package com.app.dashboard.domain.usecase

import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.repository.DashboardRepository

interface GetExpiryAlertsUseCase {
    suspend operator fun invoke(): List<ExpiryAlert>
}

class GetExpiryAlertsUseCaseImpl(private val repository: DashboardRepository) : GetExpiryAlertsUseCase {
    override suspend operator fun invoke(): List<ExpiryAlert> =
        repository.getExpiryAlerts(EXPIRY_ALERT_DAYS)

    companion object {
        const val EXPIRY_ALERT_DAYS = 7
    }
}