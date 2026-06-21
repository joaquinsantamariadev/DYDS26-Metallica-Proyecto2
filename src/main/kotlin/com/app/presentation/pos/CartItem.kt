package com.app.presentation.pos

import com.app.domain.entity.Product

data class CartItem(
    val product: Product,
    val quantity: Int,
    val subtotal: Double
)