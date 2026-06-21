package com.app.domain.usecase

import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.data.DashboardRepositoryFake
import com.app.domain.usecase.dashboard.GetExpiryAlertsUseCase
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetExpiryAlertsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetExpiryAlertsUseCase(repo)

    @Test
    fun returnsExpiryAlertsFromRepository() = runBlocking {
        val expected = listOf(ExpiryAlert(1L, "Producto A", LocalDate.now().plusDays(3), 3))
        repo.expiryAlertsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun passesExpiryAlertDaysConstantToRepository() = runBlocking {
        useCase()

        assertEquals(GetExpiryAlertsUseCase.EXPIRY_ALERT_DAYS, repo.capturedWithinDays)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}