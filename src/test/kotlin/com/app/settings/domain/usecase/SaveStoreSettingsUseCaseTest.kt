package com.app.settings.domain.usecase

import com.app.settings.data.repository.SettingsRepositoryFake
import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.usecase.SaveStoreSettingsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaveStoreSettingsUseCaseTest {
    private val repository = SettingsRepositoryFake()
    private val useCase = SaveStoreSettingsUseCaseImpl(repository)

    @Test
    fun savesStoreSettingsWhenDataIsValid() = runBlocking<Unit> {
        val input = StoreSettings(storeName = "Almacen Norte")

        useCase(input)

        assertTrue(repository.saveStoreCalled)
        assertEquals(input, repository.capturedStoreSettings)
    }

    @Test
    fun throwsWhenStoreNameIsBlank() = runBlocking<Unit> {
        val input = StoreSettings(storeName = " ")

        assertFailsWith<IllegalArgumentException> { useCase(input) }
        assertFalse(repository.saveStoreCalled)
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        val input = StoreSettings(storeName = "Almacen Norte")
        repository.shouldThrowOnSaveStore = true

        assertFailsWith<Exception> { useCase(input) }
    }
}
