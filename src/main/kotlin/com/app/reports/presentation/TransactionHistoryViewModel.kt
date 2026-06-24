package com.app.reports.presentation

import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.usecase.GetTransactionHistoryUseCase
import com.app.reports.domain.usecase.GetTransactionHistoryUseCaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        loadTransactions(isRefresh = true)
    }

    fun onFiltersChanged(filters: ReportFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadTransactions(isRefresh = true)
    }

    fun loadNextPage() {
        if (_uiState.value.hasNextPage && !_uiState.value.isLoading) {
            loadTransactions(isRefresh = false)
        }
    }

    fun refresh() {
        loadTransactions(isRefresh = true)
    }

    private fun loadTransactions(isRefresh: Boolean) {
        scope.launch {
            val pageToLoad = if (isRefresh) 0 else _uiState.value.currentPage + 1

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    currentPage = pageToLoad,
                    transactions = if (isRefresh) emptyList() else it.transactions
                )
            }

            try {
                val filters = _uiState.value.filters
                val newTransactions = getTransactionHistoryUseCase(filters, pageToLoad)
                _uiState.update {
                    val updatedList = if (isRefresh) newTransactions else it.transactions + newTransactions
                    it.copy(
                        transactions = updatedList,
                        isLoading = false,
                        hasNextPage = newTransactions.size == GetTransactionHistoryUseCaseImpl.HISTORY_PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar historial"
                    )
                }
            }
        }
    }
}
