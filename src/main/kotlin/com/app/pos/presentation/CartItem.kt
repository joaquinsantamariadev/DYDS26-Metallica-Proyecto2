package com.app.pos.presentation

import com.app.inventory.domain.entity.Product

data class CartItem(
    val product: Product,
    val quantity: Int,
    val subtotal: Double
)