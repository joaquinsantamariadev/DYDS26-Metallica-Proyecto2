package com.app.domain.usecase.dashboard

import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.repository.DashboardRepository

class GetExpiryAlertsUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): List<ExpiryAlert> =
        repository.getExpiryAlerts(EXPIRY_ALERT_DAYS)

    companion object {
        const val EXPIRY_ALERT_DAYS = 7
    }
}