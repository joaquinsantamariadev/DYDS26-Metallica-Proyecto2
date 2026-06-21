package com.app.presentation.pos

import com.app.domain.entity.CashRegisterSession

data class PosState(
    val activeSession: CashRegisterSession? = null,
    val cartItems: List<CartItem> = emptyList(),
    val cartTotal: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saleCompleted: Boolean = false
)