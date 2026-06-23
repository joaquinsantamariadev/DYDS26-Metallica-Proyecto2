package com.app.domain.usecase.settings

import com.app.data.SettingsRepositoryFake
import com.app.domain.entity.settings.SystemSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSystemSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = GetSystemSettingsUseCase(repository)

    @Test
    fun returnsSystemSettingsFromRepository() = runBlocking {
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
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking {
        repository.shouldThrowOnGetSystem = true

        assertFailsWith<Exception> { useCase() }
    }
}
