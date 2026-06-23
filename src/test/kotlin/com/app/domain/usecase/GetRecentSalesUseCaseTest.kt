package com.app.domain.usecase

import com.app.data.DashboardRepositoryFake
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.usecase.dashboard.GetRecentSalesUseCase
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecentSalesUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetRecentSalesUseCase(repo)

    @Test
    fun returnsRecentSalesFromRepository() = runBlocking {
        val expected = listOf(RecentSaleEntry(1L, LocalDateTime.now(), 3, 150.0))
        repo.recentSalesResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun passesRecentSalesLimitConstantToRepository() = runBlocking {
        useCase()

        assertEquals(GetRecentSalesUseCase.RECENT_SALES_LIMIT, repo.capturedLimit)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}