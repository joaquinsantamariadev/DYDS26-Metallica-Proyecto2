package com.app.domain.entity.sale

data class SaleItem(
    val id: Int? = null,
    val productId: Int,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)