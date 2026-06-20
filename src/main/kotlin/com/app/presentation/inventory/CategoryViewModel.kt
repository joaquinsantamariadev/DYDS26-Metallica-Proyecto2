package com.app.presentation.inventory

import com.app.domain.entity.Category
import com.app.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

class CategoryViewModel(private val categoryRepository: CategoryRepository) {
    private val viewModelScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val categories = categoryRepository.getCategories()
                _state.update { it.copy(isLoading = false, categories = categories) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Error al cargar categorías") }
            }
        }
    }

    fun addCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                categoryRepository.insertCategory(Category(name = name))
                loadAll()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al agregar categoría") }
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(category)
                loadAll()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al actualizar categoría") }
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(id)
                loadAll()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al eliminar categoría") }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}