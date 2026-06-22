package com.app.domain.usecase.report

import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.RevenueSummary
import com.app.domain.repository.ReportsRepository

class GetRevenueSummaryUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(filters: ReportFilters): RevenueSummary {
        return reportsRepository.getRevenueSummary(filters)
    }
}