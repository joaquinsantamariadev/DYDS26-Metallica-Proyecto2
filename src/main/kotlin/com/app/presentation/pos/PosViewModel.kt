package com.app.presentation.pos

import com.app.domain.entity.PaymentMethod
import com.app.domain.entity.SaleItem
import com.app.domain.repository.InventoryRepository
import com.app.domain.usecase.ScanProductUseCase
import com.app.domain.usecase.cart.CalculateCartTotalUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.sale.CompleteSaleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PosViewModel(
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val completeSaleUseCase: CompleteSaleUseCase,
    private val scanProductUseCase: ScanProductUseCase,
    private val inventoryRepository: InventoryRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    private val _state = MutableStateFlow(PosState())
    val state: StateFlow<PosState> = _state.asStateFlow()

    init {
        scope.launch {
            _state.update { it.copy(activeSession = getActiveSessionUseCase()) }
        }
    }

    fun onEvent(event: PosEvent) {
        when (event) {
            is PosEvent.AddItem -> addItem(event)
            is PosEvent.RemoveItem -> removeItem(event.productId)
            is PosEvent.UpdateQuantity -> updateQuantity(event.productId, event.quantity)
            is PosEvent.CompleteSale -> completeSale(event.paymentMethod)
            is PosEvent.SearchByName -> searchByName(event.query)
            is PosEvent.ScanBarcode -> scanBarcode(event.barcode)
            PosEvent.ClearCart -> clearCart()
            PosEvent.DismissError -> _state.update { it.copy(error = null) }
            PosEvent.AcknowledgeSale -> _state.update { it.copy(saleCompleted = false) }
            PosEvent.RefreshSession -> scope.launch {
                _state.update { it.copy(activeSession = getActiveSessionUseCase()) }
            }
        }
    }

    private fun addItem(event: PosEvent.AddItem) {
        val current = _state.value.cartItems
        val existing = current.find { it.product.id == event.product.id }
        val updated = if (existing != null) {
            val newQty = existing.quantity + event.quantity
            current.map { item ->
                if (item.product.id == event.product.id)
                    item.copy(quantity = newQty, subtotal = event.product.price * newQty)
                else item
            }
        } else {
            current + CartItem(
                product = event.product,
                quantity = event.quantity,
                subtotal = event.product.price * event.quantity
            )
        }
        _state.update { it.copy(cartItems = updated, cartTotal = totalOf(updated)) }
    }

    private fun removeItem(productId: Int) {
        val updated = _state.value.cartItems.filter { it.product.id != productId }
        _state.update { it.copy(cartItems = updated, cartTotal = totalOf(updated)) }
    }

    private fun updateQuantity(productId: Int, quantity: Int) {
        val updated = if (quantity <= 0) {
            _state.value.cartItems.filter { it.product.id != productId }
        } else {
            _state.value.cartItems.map { item ->
                if (item.product.id == productId)
                    item.copy(quantity = quantity, subtotal = item.product.price * quantity)
                else item
            }
        }
        _state.update { it.copy(cartItems = updated, cartTotal = totalOf(updated)) }
    }

    private fun completeSale(paymentMethod: PaymentMethod) {
        val session = _state.value.activeSession
        if (session == null) {
            _state.update { it.copy(error = "No hay sesión de caja activa") }
            return
        }
        val snapshot = _state.value.cartItems
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            completeSaleUseCase(session.id!!, snapshot.map { it.toSaleItem() }, paymentMethod)
                .fold(
                    onSuccess = {
                        _state.update { s ->
                            s.copy(
                                isLoading = false,
                                cartItems = emptyList(),
                                cartTotal = 0.0,
                                saleCompleted = true,
                                error = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _state.update { s -> s.copy(isLoading = false, error = e.message) }
                    }
                )
        }
    }

    private fun searchByName(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }
        scope.launch {
            val results = inventoryRepository.getProducts()
                .filter { it.name.contains(query, ignoreCase = true) }
            _state.update { it.copy(searchResults = results) }
        }
    }

    private fun scanBarcode(barcode: String) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val product = scanProductUseCase(barcode)
            _state.update { s ->
                s.copy(
                    isLoading = false,
                    searchResults = if (product != null) listOf(product) else emptyList(),
                    error = if (product == null) "Producto no encontrado para el código $barcode" else null
                )
            }
        }
    }

    private fun clearCart() {
        _state.update { it.copy(cartItems = emptyList(), cartTotal = 0.0) }
    }

    private fun totalOf(items: List<CartItem>): Double =
        calculateCartTotalUseCase(items.map { it.product.price to it.quantity })

    private fun CartItem.toSaleItem() = SaleItem(
        productId = product.id!!,
        productName = product.name,
        unitPrice = product.price,
        quantity = quantity,
        subtotal = subtotal
    )
}