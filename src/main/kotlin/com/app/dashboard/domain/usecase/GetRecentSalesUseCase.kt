package com.app.dashboard.domain.usecase

import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.repository.DashboardRepository

interface GetRecentSalesUseCase {
    suspend operator fun invoke(): List<RecentSaleEntry>
}

class GetRecentSalesUseCaseImpl(private val repository: DashboardRepository) : GetRecentSalesUseCase {
    override suspend operator fun invoke(): List<RecentSaleEntry> =
        repository.getRecentSales(RECENT_SALES_LIMIT)

    companion object {
        const val RECENT_SALES_LIMIT = 10
    }
}