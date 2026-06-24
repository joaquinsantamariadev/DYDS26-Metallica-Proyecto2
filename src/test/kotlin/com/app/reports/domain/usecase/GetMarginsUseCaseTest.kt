package com.app.reports.domain.usecase

import com.app.reports.data.repository.ReportsRepositoryFake
import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.usecase.GetMarginsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetMarginsUseCaseTest {
    private val repo = ReportsRepositoryFake()
    private val useCase = GetMarginsUseCaseImpl(repo)

    @Test
    fun returnsMarginsFromRepository() = runBlocking {
        val expected = listOf(
            MarginEntry(1L, "Producto A", "Cat 1", 50.0, 100.0, 50.0, 50.0)
        )
        repo.marginsResult = expected

        assertEquals(expected, useCase())
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() = runBlocking<Unit> {
        repo.shouldThrowError = true

        assertFailsWith<Exception> { useCase() }
    }
}
