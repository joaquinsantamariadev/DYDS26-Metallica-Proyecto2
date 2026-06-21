package com.app.presentation.dashboard

import com.app.domain.usecase.dashboard.GetDashboardMetricsUseCase
import com.app.domain.usecase.dashboard.GetExpiryAlertsUseCase
import com.app.domain.usecase.dashboard.GetLowStockAlertsUseCase
import com.app.domain.usecase.dashboard.GetRecentSalesUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DashboardViewModel(
    private val getMetrics: GetDashboardMetricsUseCase,
    private val getLowStockAlerts: GetLowStockAlertsUseCase,
    private val getExpiryAlerts: GetExpiryAlertsUseCase,
    private val getRecentSales: GetRecentSalesUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                coroutineScope {
                    val metrics = async { getMetrics() }
                    val stock = async { getLowStockAlerts() }
                    val expiry = async { getExpiryAlerts() }
                    val sales = async { getRecentSales() }
                    _uiState.update {
                        it.copy(
                            metrics = metrics.await(),
                            stockAlerts = stock.await(),
                            expiryAlerts = expiry.await(),
                            recentSales = sales.await(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}