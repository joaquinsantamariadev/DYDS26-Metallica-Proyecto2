package com.app.settings.domain.usecase

import com.app.settings.data.repository.SettingsRepositoryFake
import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.usecase.SaveSystemSettingsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveSystemSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = SaveSystemSettingsUseCaseImpl(repository)

    @Test
    fun savesSystemSettingsWhenDataIsValid() = runBlocking<Unit> {
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
    fun throwsWhenLowStockThresholdIsInvalid() = runBlocking<Unit> {
        val input = SystemSettings(defaultLowStockThreshold = 0)

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveSystemCalled)
    }

    @Test
    fun throwsWhenExpiryAlertDaysIsInvalid() = runBlocking<Unit> {
        val input = SystemSettings(defaultLowStockThreshold = 5, expiryAlertDays = 0)

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveSystemCalled)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        val input = SystemSettings()
        repository.shouldThrowOnSaveSystem = true

        assertFailsWith<Exception> { useCase(input) }
    }
}
