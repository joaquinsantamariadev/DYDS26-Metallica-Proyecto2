package com.app.domain.usecase.dashboard

import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.repository.DashboardRepository

class GetRecentSalesUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): List<RecentSaleEntry> =
        repository.getRecentSales(RECENT_SALES_LIMIT)

    companion object {
        const val RECENT_SALES_LIMIT = 10
    }
}