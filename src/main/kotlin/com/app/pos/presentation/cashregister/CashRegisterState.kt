package com.app.pos.presentation.cashregister

import com.app.pos.domain.entity.CashRegisterSession

data class CashRegisterState(
    val activeSession: CashRegisterSession? = null,
    val sessionHistory: List<CashRegisterSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)