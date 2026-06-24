package com.app.dashboard.domain.usecase

import com.app.dashboard.data.repository.DashboardRepositoryFake
import com.app.dashboard.domain.entity.StockAlert
import com.app.dashboard.domain.usecase.GetLowStockAlertsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetLowStockAlertsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetLowStockAlertsUseCaseImpl(repo)

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