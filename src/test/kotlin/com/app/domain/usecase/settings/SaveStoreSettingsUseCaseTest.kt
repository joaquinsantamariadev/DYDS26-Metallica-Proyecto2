package com.app.domain.usecase.settings

import com.app.data.SettingsRepositoryFake
import com.app.domain.entity.settings.StoreSettings
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveStoreSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = SaveStoreSettingsUseCase(repository)

    @Test
    fun savesStoreSettingsWhenDataIsValid() = runBlocking {
        val input = StoreSettings(storeName = "Almacen Norte")

        useCase(input)

        assertTrue(repository.saveStoreCalled)
        assertEquals(input, repository.capturedStoreSettings)
    }

    @Test
    fun throwsWhenStoreNameIsBlank() = runBlocking {
        val input = StoreSettings(storeName = " ")

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveStoreCalled)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking {
        val input = StoreSettings(storeName = "Almacen Norte")
        repository.shouldThrowOnSaveStore = true

        assertFailsWith<Exception> { useCase(input) }
    }
}
