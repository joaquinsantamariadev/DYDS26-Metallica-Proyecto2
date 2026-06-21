package com.app.presentation.pos

import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.Product

data class PosState(
    val activeSession: CashRegisterSession? = null,
    val cartItems: List<CartItem> = emptyList(),
    val cartTotal: Double = 0.0,
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saleCompleted: Boolean = false
)