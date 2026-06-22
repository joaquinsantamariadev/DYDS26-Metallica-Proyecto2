package com.app.domain.entity.reports

data class TransactionItemDetail(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)