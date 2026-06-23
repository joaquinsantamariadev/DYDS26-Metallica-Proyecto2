package com.app.domain.usecase

import com.app.data.ReportsRepositoryFake
import com.app.domain.entity.report.ProductRotationEntry
import com.app.domain.entity.report.ReportFilters
import com.app.domain.usecase.report.GetProductRotationUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetProductRotationUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetProductRotationUseCase(repo)

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

        assertEquals(GetProductRotationUseCase.ROTATION_TOP_N, repo.capturedTopN)
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
