package com.app.reports.domain.usecase

import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.TransactionHistoryEntry
import com.app.reports.domain.repository.ReportsRepository

interface GetTransactionHistoryUseCase {
    suspend operator fun invoke(filters: ReportFilters, page: Int): List<TransactionHistoryEntry>
}

class GetTransactionHistoryUseCaseImpl(
    private val reportsRepository: ReportsRepository
) : GetTransactionHistoryUseCase {
    override suspend operator fun invoke(filters: ReportFilters, page: Int): List<TransactionHistoryEntry> {
        val offset = page * HISTORY_PAGE_SIZE
        return reportsRepository.getTransactionHistory(filters, HISTORY_PAGE_SIZE, offset)
    }

    companion object {
        const val HISTORY_PAGE_SIZE = 50
    }
}