package com.app.presentation.reports

import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.TransactionHistoryEntry

data class TransactionHistoryUiState(
    val transactions: List<TransactionHistoryEntry> = emptyList(),
    val filters: ReportFilters = ReportFilters.default(),
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
