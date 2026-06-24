package com.app.reports.presentation

import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.usecase.GetMarginsUseCase
import com.app.reports.domain.usecase.GetProductRotationUseCase
import com.app.reports.domain.usecase.GetRevenueSummaryUseCase
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

class StatisticsViewModel(
    private val getRevenueSummaryUseCase: GetRevenueSummaryUseCase,
    private val getProductRotationUseCase: GetProductRotationUseCase,
    private val getMarginsUseCase: GetMarginsUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadMargins()
        loadTimeDependentStats()
    }

    fun onFiltersChanged(filters: ReportFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadTimeDependentStats()
    }

    private fun loadTimeDependentStats() {
        scope.launch {
            val filters = _uiState.value.filters
            _uiState.update {
                it.copy(
                    isLoadingRevenue = true,
                    isLoadingRotation = true,
                    error = null
                )
            }

            try {
                coroutineScope {
                    val revenueDef = async { getRevenueSummaryUseCase(filters) }
                    val rotationDef = async { getProductRotationUseCase(filters) }

                    val revenue = revenueDef.await()
                    val rotation = rotationDef.await()

                    _uiState.update {
                        it.copy(
                            revenueSummary = revenue,
                            rotation = rotation,
                            isLoadingRevenue = false,
                            isLoadingRotation = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingRevenue = false,
                        isLoadingRotation = false,
                        error = e.message ?: "Error al cargar estadísticas"
                    )
                }
            }
        }
    }

    private fun loadMargins() {
        scope.launch {
            _uiState.update { it.copy(isLoadingMargins = true, error = null) }
            try {
                val margins = getMarginsUseCase()
                _uiState.update {
                    it.copy(margins = margins, isLoadingMargins = false)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMargins = false,
                        error = e.message ?: "Error al cargar márgenes"
                    )
                }
            }
        }
    }
}
