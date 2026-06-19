package com.app.presentation.inventory

import com.app.domain.entity.Product
import com.app.domain.repository.InventoryRepository
import com.app.domain.usecase.ScanProductUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

class InventoryViewModel(
    private val repository: InventoryRepository,
    private val scanProductUseCase: ScanProductUseCase
) {
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val products = repository.getProducts()
                _state.update { it.copy(isLoading = false, products = products) }
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
                    val updatedProducts = repository.getProducts()
                    _state.update { it.copy(isLoading = false, products = updatedProducts) }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = "Producto no encontrado (ni local ni en internet)"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al escanear código") }
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
