package com.app.domain.usecase

import com.app.domain.entity.dashboard.StockAlert
import com.app.domain.usecase.dashboard.GetLowStockAlertsUseCase
import com.app.presentation.dashboard.DashboardRepositoryFake
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetLowStockAlertsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetLowStockAlertsUseCase(repo)

    @Test
    fun returnsStockAlertsFromRepository() = runBlocking {
        val expected = listOf(StockAlert(1L, "Producto A", 2, 5))
        repo.stockAlertsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}