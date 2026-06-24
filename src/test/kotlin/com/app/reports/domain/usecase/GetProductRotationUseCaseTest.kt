package com.app.reports.domain.usecase

import com.app.reports.data.repository.ReportsRepositoryFake
import com.app.reports.domain.entity.ProductRotationEntry
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.usecase.GetProductRotationUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetProductRotationUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetProductRotationUseCaseImpl(repo)

    @Test
    fun returnsRotationFromRepository() = runBlocking {
        val expected = listOf(
            ProductRotationEntry(1L, "Producto A", "Cat 1", 50, 500.0)
        )
        repo.productRotationResult = expected

        assertEquals(expected, useCase(ReportFilters.default()))
    }

    @Test
    fun passesRotationTopNConstantToRepository() = runBlocking {
        useCase(ReportFilters.default())

        assertEquals(GetProductRotationUseCaseImpl.ROTATION_TOP_N, repo.capturedTopN)
    }

    @Test
    fun passesFiltersToRepository() = runBlocking {
        val filters = ReportFilters.default()
        useCase(filters)

        assertEquals(filters, repo.capturedRotationFilters)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase(ReportFilters.default()) }
    }
}
