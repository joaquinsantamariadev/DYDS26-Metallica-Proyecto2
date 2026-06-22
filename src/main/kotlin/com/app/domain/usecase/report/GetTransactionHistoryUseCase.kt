package com.app.domain.usecase.report

import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.TransactionHistoryEntry
import com.app.domain.repository.ReportsRepository

class GetTransactionHistoryUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(filters: ReportFilters, page: Int): List<TransactionHistoryEntry> {
        val offset = page * HISTORY_PAGE_SIZE
        return reportsRepository.getTransactionHistory(filters, HISTORY_PAGE_SIZE, offset)
    }

    companion object {
        const val HISTORY_PAGE_SIZE = 50
    }
}