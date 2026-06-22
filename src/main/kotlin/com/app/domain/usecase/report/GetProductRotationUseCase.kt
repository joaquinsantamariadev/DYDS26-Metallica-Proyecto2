package com.app.domain.usecase.report

import com.app.domain.entity.report.ProductRotationEntry
import com.app.domain.entity.report.ReportFilters
import com.app.domain.repository.ReportsRepository

class GetProductRotationUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(filters: ReportFilters): List<ProductRotationEntry> {
        return reportsRepository.getProductRotation(filters, ROTATION_TOP_N)
    }

    companion object {
        const val ROTATION_TOP_N = 20
    }
}