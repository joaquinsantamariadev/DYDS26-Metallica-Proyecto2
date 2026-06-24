package com.app.reports.domain.entity

data class TransactionItemDetail(
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)