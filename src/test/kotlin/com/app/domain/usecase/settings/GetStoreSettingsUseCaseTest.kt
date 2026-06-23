package com.app.domain.usecase.settings

import com.app.data.SettingsRepositoryFake
import com.app.domain.entity.settings.StoreSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetStoreSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = GetStoreSettingsUseCase(repository)

    @Test
    fun returnsStoreSettingsFromRepository() = runBlocking {
        val expected = StoreSettings(
            storeName = "Kiosco Centro",
            address = "Calle 123",
            phone = "123456",
            currency = "ARS",
            logoPath = "logo.png"
        )
        repository.storeSettingsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking {
        repository.shouldThrowOnGetStore = true

        assertFailsWith<Exception> { useCase() }
    }
}
