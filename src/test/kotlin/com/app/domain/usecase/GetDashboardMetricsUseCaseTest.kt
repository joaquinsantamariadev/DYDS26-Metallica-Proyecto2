package com.app.domain.usecase

import com.app.data.DashboardRepositoryFake
import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.usecase.dashboard.GetDashboardMetricsUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetDashboardMetricsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetDashboardMetricsUseCase(repo)

    @Test
    fun returnsMetricsFromRepository() = runBlocking {
        val expected = DashboardMetrics(10, 500.0, 3, 150.0, 2, 1, true)
        repo.metricsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}