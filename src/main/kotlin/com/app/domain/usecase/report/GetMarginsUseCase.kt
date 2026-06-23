package com.app.domain.usecase.report

import com.app.domain.entity.report.MarginEntry
import com.app.domain.repository.ReportsRepository

class GetMarginsUseCase(
    private val reportsRepository: ReportsRepository
) {
    suspend operator fun invoke(): List<MarginEntry> {
        return reportsRepository.getMargins()
    }
}
