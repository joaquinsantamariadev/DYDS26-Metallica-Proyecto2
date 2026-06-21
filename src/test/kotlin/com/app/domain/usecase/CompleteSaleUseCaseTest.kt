package com.app.domain.usecase

import com.app.data.FakeInventoryRepository
import com.app.domain.entity.PaymentMethod
import com.app.domain.entity.sale.SaleItem
import com.app.domain.usecase.cart.ValidateCartStockUseCase
import com.app.domain.usecase.sale.CompleteSaleUseCase
import com.app.fakes.SaleRepositoryFake
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class CompleteSaleUseCaseTest {

    private val inventoryRepo = FakeInventoryRepository()
    private val saleRepo = SaleRepositoryFake()
    private val validateStock = ValidateCartStockUseCase(inventoryRepo)
    private val useCase = CompleteSaleUseCase(validateStock, inventoryRepo, saleRepo)

    @Test
    fun happyPath_salePersistedAndStockReduced() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, stock = 10))

        val items = listOf(saleItem(productId = 1, quantity = 3))
        val result = useCase(sessionId = 1, items = items, paymentMethod = PaymentMethod.CASH)

        assertTrue(result.isSuccess)
        assertNotNull(saleRepo.savedSale)
        assertTrue(inventoryRepo.updateStockCalled)
        assertEquals(7, inventoryRepo.stockUpdated[1])
    }

    @Test
    fun insufficientStock_returnsFailureWithoutPersisting() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, stock = 1))

        val items = listOf(saleItem(productId = 1, quantity = 5))
        val result = useCase(sessionId = 1, items = items, paymentMethod = PaymentMethod.CASH)

        assertTrue(result.isFailure)
        assertNull(saleRepo.savedSale)
    }

    @Test
    fun saleRepositoryThrows_returnsFailure() = runBlocking {
        inventoryRepo.productsList.add(product(id = 1, stock = 10))
        saleRepo.shouldThrowError = true

        val items = listOf(saleItem(productId = 1, quantity = 1))
        val result = useCase(sessionId = 1, items = items, paymentMethod = PaymentMethod.CARD)

        assertTrue(result.isFailure)
        assertNull(saleRepo.savedSale)
    }

    private fun product(id: Int, stock: Int) = com.app.domain.entity.Product(
        id = id, barcode = null, name = "Producto $id", categoryId = null,
        price = 10.0, cost = 5.0, stock = stock
    )

    private fun saleItem(productId: Int, quantity: Int) = SaleItem(
        productId = productId, productName = "Producto $productId",
        unitPrice = 10.0, quantity = quantity, subtotal = 10.0 * quantity
    )
}