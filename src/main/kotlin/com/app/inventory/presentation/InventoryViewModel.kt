package com.app.inventory.presentation

import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.inventory.domain.entity.Product
import com.app.inventory.domain.repository.InventoryRepository
import com.app.inventory.domain.usecase.ScanProductUseCase
import com.app.exchangerate.domain.usecase.GetExchangeRateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryState(
    val isLoading: Boolean = false,
    val allProducts: List<Product> = emptyList(),
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val exchangeRate: ExchangeRate? = null,
    val exchangeRateUnavailable: Boolean = false
)

class InventoryViewModel(
    private val inventoryRepository: InventoryRepository,
    private val scanProductUseCase: ScanProductUseCase,
    private val getExchangeRateUseCase: GetExchangeRateUseCase   // ← agregar
) {
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                coroutineScope {
                    val productsDef = async { inventoryRepository.getProducts() }
                    val rateDef = async { runCatching { getExchangeRateUseCase() } }

                    val products = productsDef.await()
                    val rateResult = rateDef.await()

                    _state.update {
                        it.copy(
                            isLoading = false,
                            allProducts = products,
                            products = applySearch(products, it.searchQuery),
                            exchangeRate = rateResult.getOrNull(),
                            exchangeRateUnavailable = rateResult.isFailure
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al cargar productos") }
            }
        }
    }

    fun scanBarcode(barcode: String) {
        if (barcode.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val product = scanProductUseCase(barcode)
                if (product != null) {
                    val products = inventoryRepository.getProducts()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allProducts = products,
                            products = applySearch(products, it.searchQuery)
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Producto no encontrado") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al escanear código") }
            }
        }
    }

    fun search(query: String) {
        _state.update { it.copy(searchQuery = query, products = applySearch(it.allProducts, query)) }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                inventoryRepository.updateProduct(product)
                loadAll()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar producto") }
            }
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            try {
                inventoryRepository.deleteProduct(id)
                loadAll()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al eliminar producto") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun applySearch(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return products
        val q = query.lowercase()
        return products.filter {
            it.name.lowercase().contains(q) || it.barcode?.contains(q) == true
        }
    }
}