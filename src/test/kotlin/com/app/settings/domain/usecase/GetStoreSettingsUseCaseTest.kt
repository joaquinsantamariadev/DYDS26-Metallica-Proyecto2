package com.app.settings.domain.usecase

import com.app.settings.data.repository.SettingsRepositoryFake
import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.usecase.GetStoreSettingsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetStoreSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = GetStoreSettingsUseCaseImpl(repository)

    @Test
    fun returnsStoreSettingsFromRepository() = runBlocking<Unit> {
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
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repository.shouldThrowOnGetStore = true

        assertFailsWith<Exception> { useCase() }
    }
}
