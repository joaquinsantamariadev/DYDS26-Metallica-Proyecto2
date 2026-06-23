package com.app.domain.usecase.settings

import com.app.data.SettingsRepositoryFake
import com.app.domain.entity.settings.SystemSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveSystemSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = SaveSystemSettingsUseCase(repository)

    @Test
    fun savesSystemSettingsWhenDataIsValid() = runBlocking {
        val input = SystemSettings(
            defaultLowStockThreshold = 5,
            expiryAlertDays = 10,
            historyPageSize = 100,
            rotationTopN = 30
        )

        useCase(input)

        assertTrue(repository.saveSystemCalled)
        assertEquals(input, repository.capturedSystemSettings)
    }

    @Test
    fun throwsWhenLowStockThresholdIsInvalid() = runBlocking {
        val input = SystemSettings(defaultLowStockThreshold = 0)

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveSystemCalled)
    }

    @Test
    fun throwsWhenExpiryAlertDaysIsInvalid() = runBlocking {
        val input = SystemSettings(defaultLowStockThreshold = 5, expiryAlertDays = 0)

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveSystemCalled)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking {
        val input = SystemSettings()
        repository.shouldThrowOnSaveSystem = true

        assertFailsWith<Exception> { useCase(input) }
    }
}
