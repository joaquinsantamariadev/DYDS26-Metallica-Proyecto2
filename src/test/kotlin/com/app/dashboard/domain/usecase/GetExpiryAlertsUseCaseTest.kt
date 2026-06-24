package com.app.dashboard.domain.usecase

import com.app.dashboard.data.repository.DashboardRepositoryFake
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.usecase.GetExpiryAlertsUseCaseImpl
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetExpiryAlertsUseCaseTest {
    private val repo = DashboardRepositoryFake()
    private val useCase = GetExpiryAlertsUseCaseImpl(repo)

    @Test
    fun returnsExpiryAlertsFromRepository() = runBlocking {
        val expected = listOf(ExpiryAlert(1L, "Producto A", LocalDate.now().plusDays(3), 3))
        repo.expiryAlertsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun passesExpiryAlertDaysConstantToRepository() = runBlocking {
        useCase()

        assertEquals(GetExpiryAlertsUseCaseImpl.EXPIRY_ALERT_DAYS, repo.capturedWithinDays)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}