package com.app.settings.domain.usecase

import com.app.settings.data.repository.SettingsRepositoryFake
import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.usecase.GetSystemSettingsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSystemSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = GetSystemSettingsUseCaseImpl(repository)

    @Test
    fun returnsSystemSettingsFromRepository() = runBlocking<Unit> {
        val expected = SystemSettings(
            defaultLowStockThreshold = 8,
            expiryAlertDays = 15,
            historyPageSize = 80,
            rotationTopN = 25
        )
        repository.systemSettingsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repository.shouldThrowOnGetSystem = true

        assertFailsWith<Exception> { useCase() }
    }
}
