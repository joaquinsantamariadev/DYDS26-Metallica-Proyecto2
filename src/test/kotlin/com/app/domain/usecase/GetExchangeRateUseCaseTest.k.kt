package com.app.domain.usecase.exchangerate

import com.app.data.ExchangeRateRepositoryFake
import com.app.domain.entity.ExchangeRate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetExchangeRateUseCaseTest {

    private lateinit var repository: ExchangeRateRepositoryFake
    private lateinit var useCase: GetExchangeRateUseCase

    @Before
    fun setUp() {
        repository = ExchangeRateRepositoryFake()
        useCase = GetExchangeRateUseCase(repository)
    }

    @Test
    fun returnsExchangeRateFromRepository() {
        runTest {
            val expected = ExchangeRate(currencyPair = "USD/ARS", rate = 1020.0, lastUpdated = 0L)
            repository.blueRate = expected

            val result = useCase()

            assertEquals(expected, result)
        }
    }

    @Test
    fun propagatesExceptionWhenRepositoryThrows() {
        runTest {
            repository.shouldThrowError = true

            var threw = false
            try {
                useCase()
            } catch (e: Exception) {
                threw = true
            }

            assertEquals(true, threw)
        }
    }
}