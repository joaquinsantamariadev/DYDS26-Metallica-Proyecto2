package com.app.dashboard.domain.usecase

import com.app.dashboard.data.repository.DashboardRepositoryFake
import com.app.dashboard.domain.entity.DashboardMetrics
import com.app.dashboard.domain.usecase.GetDashboardMetricsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetDashboardMetricsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetDashboardMetricsUseCaseImpl(repo)

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