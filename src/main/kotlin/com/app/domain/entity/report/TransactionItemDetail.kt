package com.app.domain.entity.report

data class TransactionItemDetail(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)