package com.app.domain.usecase

import com.app.domain.usecase.cart.CalculateCartTotalUseCase
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateCartTotalUseCaseTest {

    private val useCase = CalculateCartTotalUseCase()

    @Test
    fun multipleItems_returnsCorrectSum() {
        val result = useCase(listOf(10.0 to 2, 5.0 to 3, 20.0 to 1))

        assertEquals(55.0, result)
    }

    @Test
    fun emptyList_returnsZero() {
        val result = useCase(emptyList())

        assertEquals(0.0, result)
    }
}