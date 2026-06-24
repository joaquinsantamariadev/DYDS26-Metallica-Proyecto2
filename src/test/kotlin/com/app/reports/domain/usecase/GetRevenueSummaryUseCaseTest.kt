package com.app.reports.domain.usecase

import com.app.reports.data.repository.ReportsRepositoryFake
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.RevenueSummary
import com.app.reports.domain.usecase.GetRevenueSummaryUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRevenueSummaryUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetRevenueSummaryUseCaseImpl(repo)

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

        assertEquals(filters, repo.capturedRevenueFilters)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase(ReportFilters.default()) }
    }
}
