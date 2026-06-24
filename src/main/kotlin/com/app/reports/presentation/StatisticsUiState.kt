package com.app.reports.presentation

import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.entity.ProductRotationEntry
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.RevenueSummary

data class StatisticsUiState(
    val revenueSummary: RevenueSummary? = null,
    val rotation: List<ProductRotationEntry> = emptyList(),
    val margins: List<MarginEntry> = emptyList(),
    val filters: ReportFilters = ReportFilters.default(),
    val isLoadingRevenue: Boolean = false,
    val isLoadingRotation: Boolean = false,
    val isLoadingMargins: Boolean = false,
    val error: String? = null
)
