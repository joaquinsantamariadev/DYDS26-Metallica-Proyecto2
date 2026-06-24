package com.app.reports.domain.usecase

import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.repository.ReportsRepository

interface GetMarginsUseCase {
    suspend operator fun invoke(): List<MarginEntry>
}

class GetMarginsUseCaseImpl(
    private val reportsRepository: ReportsRepository
) : GetMarginsUseCase {
    override suspend operator fun invoke(): List<MarginEntry> {
        return reportsRepository.getMargins()
    }
}
