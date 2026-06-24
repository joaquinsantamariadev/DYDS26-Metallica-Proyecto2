package com.app.reports.domain.usecase

import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.RevenueSummary
import com.app.reports.domain.repository.ReportsRepository

interface GetRevenueSummaryUseCase {
    suspend operator fun invoke(filters: ReportFilters): RevenueSummary
}

class GetRevenueSummaryUseCaseImpl(
    private val reportsRepository: ReportsRepository
) : GetRevenueSummaryUseCase {
    override suspend operator fun invoke(filters: ReportFilters): RevenueSummary {
        return reportsRepository.getRevenueSummary(filters)
    }
}