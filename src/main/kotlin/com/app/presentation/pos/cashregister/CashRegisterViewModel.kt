package com.app.presentation.pos.cashregister

import com.app.domain.repository.CashRegisterRepository
import com.app.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.cashregister.OpenCashRegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CashRegisterViewModel(
    private val openCashRegisterUseCase: OpenCashRegisterUseCase,
    private val closeCashRegisterUseCase: CloseCashRegisterUseCase,
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val cashRegisterRepository: CashRegisterRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(CashRegisterState())
    val state: StateFlow<CashRegisterState> = _state.asStateFlow()

    init {
        scope.launch { loadAll() }
    }

    fun onEvent(event: CashRegisterEvent) {
        when (event) {
            is CashRegisterEvent.OpenSession -> openSession(event.openingAmount)
            is CashRegisterEvent.CloseSession -> closeSession(event.closingAmount)
            CashRegisterEvent.LoadHistory -> scope.launch { loadAll() }
            CashRegisterEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private suspend fun loadAll() {
        _state.update { it.copy(isLoading = true) }
        val active = getActiveSessionUseCase()
        val history = cashRegisterRepository.getAll()
        _state.update { it.copy(isLoading = false, activeSession = active, sessionHistory = history) }
    }

    private fun openSession(openingAmount: Double) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            openCashRegisterUseCase(openingAmount).fold(
                onSuccess = { session ->
                    val history = cashRegisterRepository.getAll()
                    _state.update { it.copy(isLoading = false, activeSession = session, sessionHistory = history, error = null) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }

    private fun closeSession(closingAmount: Double) {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            closeCashRegisterUseCase(closingAmount).fold(
                onSuccess = {
                    val history = cashRegisterRepository.getAll()
                    _state.update { it.copy(isLoading = false, activeSession = null, sessionHistory = history, error = null) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }
}