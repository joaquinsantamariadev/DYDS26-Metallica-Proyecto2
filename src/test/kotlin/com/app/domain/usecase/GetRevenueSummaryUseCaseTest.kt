package com.app.domain.usecase

import com.app.data.ReportsRepositoryFake
import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.RevenueSummary
import com.app.domain.usecase.report.GetRevenueSummaryUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRevenueSummaryUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetRevenueSummaryUseCase(repo)

    @Test
    fun returnsRevenueSummaryFromRepository() = runBlocking {
        val expected = RevenueSummary(1000.0, 5, 200.0, emptyList())
        repo.revenueSummaryResult = expected

        assertEquals(expected, useCase(ReportFilters.default()))
    }

    @Test
    fun passesFiltersToRepository() = runBlocking {
        val filters = ReportFilters.default()
        useCase(filters)

        assertEquals(filters, repo.capturedFilters)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase(ReportFilters.default()) }
    }
}
