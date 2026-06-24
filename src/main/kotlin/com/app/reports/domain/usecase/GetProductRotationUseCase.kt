package com.app.reports.domain.usecase

import com.app.reports.domain.entity.ProductRotationEntry
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.repository.ReportsRepository

interface GetProductRotationUseCase {
    suspend operator fun invoke(filters: ReportFilters): List<ProductRotationEntry>
}

class GetProductRotationUseCaseImpl(
    private val reportsRepository: ReportsRepository
) : GetProductRotationUseCase {
    override suspend operator fun invoke(filters: ReportFilters): List<ProductRotationEntry> {
        return reportsRepository.getProductRotation(filters, ROTATION_TOP_N)
    }

    companion object {
        const val ROTATION_TOP_N = 20
    }
}