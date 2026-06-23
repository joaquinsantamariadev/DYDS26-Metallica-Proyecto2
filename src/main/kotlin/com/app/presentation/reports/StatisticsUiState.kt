package com.app.presentation.reports

import com.app.domain.entity.report.MarginEntry
import com.app.domain.entity.report.ProductRotationEntry
import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.RevenueSummary

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
