package com.app.dashboard.presentation

import com.app.dashboard.domain.usecase.GetDashboardMetricsUseCase
import com.app.dashboard.domain.usecase.GetExpiryAlertsUseCase
import com.app.dashboard.domain.usecase.GetLowStockAlertsUseCase
import com.app.dashboard.domain.usecase.GetRecentSalesUseCase
import com.app.exchangerate.domain.usecase.GetExchangeRateUseCase
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
    private val getExchangeRateUseCase: GetExchangeRateUseCase,
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
                    val rateDef = async { runCatching { getExchangeRateUseCase() } }

                    val rateResult = rateDef.await()

                    _uiState.update {
                        it.copy(
                            metrics = metrics.await(),
                            stockAlerts = stock.await(),
                            expiryAlerts = expiry.await(),
                            recentSales = sales.await(),
                            exchangeRate = rateResult.getOrNull(),
                            exchangeRateUnavailable = rateResult.isFailure,
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