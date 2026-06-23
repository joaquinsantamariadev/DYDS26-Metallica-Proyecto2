package com.app.domain.usecase

import com.app.data.InventoryRepositoryFake
import com.app.domain.entity.Product
import com.app.domain.usecase.cart.ValidateCartStockUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ValidateCartStockUseCaseTest {

    private val inventoryRepo = InventoryRepositoryFake()
    private val useCase = ValidateCartStockUseCase(inventoryRepo)

    @Test
    fun allStockAvailable_returnsSuccess() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, stock = 10))
        inventoryRepo.productsList.add(product(id = 2, stock = 5))

        val result = useCase(listOf(1 to 3, 2 to 5))

        assertTrue(result.isSuccess)
    }

    @Test
    fun oneProductWithInsufficientStock_returnsFailure() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, stock = 2))

        val result = useCase(listOf(1 to 5))

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        Unit
    }

    @Test
    fun multipleProductsWithInsufficientStock_failureListsAll() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, name = "Leche", stock = 1))
        inventoryRepo.productsList.add(product(id = 2, name = "Pan", stock = 0))

        val result = useCase(listOf(1 to 5, 2 to 3))

        assertTrue(result.isFailure)
        val message = result.exceptionOrNull()!!.message!!
        assertTrue(message.contains("Leche"))
        assertTrue(message.contains("Pan"))
    }

    private fun product(id: Int, stock: Int, name: String = "Producto $id") = Product(
        id = id, barcode = null, name = name, categoryId = null,
        price = 10.0, cost = 5.0, stock = stock
    )
}