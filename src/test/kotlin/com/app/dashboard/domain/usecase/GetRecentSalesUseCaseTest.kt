package com.app.dashboard.domain.usecase

import com.app.dashboard.data.repository.DashboardRepositoryFake
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.usecase.GetRecentSalesUseCaseImpl
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetRecentSalesUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetRecentSalesUseCaseImpl(repo)

    @Test
    fun returnsRecentSalesFromRepository() = runBlocking {
        val expected = listOf(RecentSaleEntry(1L, LocalDateTime.now(), 3, 150.0))
        repo.recentSalesResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun passesRecentSalesLimitConstantToRepository() = runBlocking {
        useCase()

        assertEquals(GetRecentSalesUseCaseImpl.RECENT_SALES_LIMIT, repo.capturedLimit)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}