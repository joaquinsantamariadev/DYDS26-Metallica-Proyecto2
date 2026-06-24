package com.app.reports.presentation

import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.TransactionHistoryEntry

data class TransactionHistoryUiState(
    val transactions: List<TransactionHistoryEntry> = emptyList(),
    val filters: ReportFilters = ReportFilters.default(),
    val currentPage: Int = 0,
    val hasNextPage: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
