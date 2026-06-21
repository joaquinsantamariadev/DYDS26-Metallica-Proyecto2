package com.app.presentation.pos.cashregister

import com.app.domain.entity.CashRegisterSession

data class CashRegisterState(
    val activeSession: CashRegisterSession? = null,
    val sessionHistory: List<CashRegisterSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)