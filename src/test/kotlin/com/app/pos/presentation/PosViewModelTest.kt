package com.app.pos.presentation

import com.app.inventory.data.repository.InventoryRepositoryFake
import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.entity.PaymentMethod
import com.app.inventory.domain.entity.Product
import com.app.pos.domain.entity.SessionStatus
import com.app.inventory.domain.usecase.ScanProductUseCaseImpl
import com.app.pos.domain.usecase.cart.ValidateCartStockUseCaseImpl
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCaseImpl
import com.app.pos.domain.usecase.sale.CompleteSaleUseCaseImpl
import com.app.pos.data.repository.CashRegisterRepositoryFake
import com.app.pos.data.repository.SaleRepositoryFake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import java.time.LocalDateTime
import kotlin.test.*

class PosViewModelTest {

    private val inventoryRepo = InventoryRepositoryFake()
    private val saleRepo = SaleRepositoryFake()
    private val cashRegisterRepo = CashRegisterRepositoryFake()

    private val validateStock = ValidateCartStockUseCaseImpl(inventoryRepo)
    private val completeSale = CompleteSaleUseCaseImpl(validateStock, inventoryRepo, saleRepo)
    private val getActiveSession = GetActiveSessionUseCaseImpl(cashRegisterRepo)
    private val scanProduct = ScanProductUseCaseImpl(inventoryRepo)

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildViewModel() = PosViewModel(
        getActiveSessionUseCase = getActiveSession,
        completeSaleUseCase = completeSale,
        scanProductUseCase = scanProduct,
        inventoryRepository = inventoryRepo,
        scope = CoroutineScope(UnconfinedTestDispatcher())
    )

    @Test
    fun addItem_cartItemsUpdatedAndTotalRecalculated() {
        val vm = buildViewModel()
        val product = product(id = 1, price = 10.0)

        vm.onEvent(PosEvent.AddItem(product, 3))

        val state = vm.state.value
        assertEquals(1, state.cartItems.size)
        assertEquals(3, state.cartItems.first().quantity)
        assertEquals(30.0, state.cartTotal)
    }

    @Test
    fun removeItem_itemEliminatedAndTotalUpdated() {
        val vm = buildViewModel()
        val product = product(id = 1, price = 10.0)

        vm.onEvent(PosEvent.AddItem(product, 2))
        vm.onEvent(PosEvent.RemoveItem(1))

        assertTrue(vm.state.value.cartItems.isEmpty())
        assertEquals(0.0, vm.state.value.cartTotal)
    }

    @Test
    fun completeSale_withoutActiveSession_errorSetAndSaleNotCompleted() {
        val vm = buildViewModel()
        val product = product(id = 1, price = 10.0)
        vm.onEvent(PosEvent.AddItem(product, 1))

        vm.onEvent(PosEvent.CompleteSale(PaymentMethod.CASH))

        assertFalse(vm.state.value.saleCompleted)
        assertNotNull(vm.state.value.error)
    }

    @Test
    fun completeSale_withActiveSessionAndStock_saleCompletedAndCartCleared() {
        inventoryRepo.productsList.add(product(id = 1, price = 10.0, stock = 5))
        cashRegisterRepo.activeSession = session()
        val vm = buildViewModel()

        vm.onEvent(PosEvent.AddItem(product(id = 1, price = 10.0, stock = 5), 2))
        vm.onEvent(PosEvent.CompleteSale(PaymentMethod.CASH))

        assertTrue(vm.state.value.saleCompleted)
        assertTrue(vm.state.value.cartItems.isEmpty())
    }

    @Test
    fun completeSale_saleRepositoryThrows_errorPropagatedAndCartIntact() {
        inventoryRepo.productsList.add(product(id = 1, price = 10.0, stock = 5))
        cashRegisterRepo.activeSession = session()
        saleRepo.shouldThrowError = true
        val vm = buildViewModel()

        vm.onEvent(PosEvent.AddItem(product(id = 1, price = 10.0, stock = 5), 1))
        vm.onEvent(PosEvent.CompleteSale(PaymentMethod.CASH))

        assertFalse(vm.state.value.saleCompleted)
        assertNotNull(vm.state.value.error)
        assertEquals(1, vm.state.value.cartItems.size)
    }

    private fun product(id: Int, price: Double, stock: Int = 10) = Product(
        id = id, barcode = null, name = "Producto $id", categoryId = null,
        price = price, cost = price / 2, stock = stock
    )

    private fun session() = CashRegisterSession(
        id = 1, openingAmount = 100.0,
        openedAt = LocalDateTime.now(), status = SessionStatus.OPEN
    )
}