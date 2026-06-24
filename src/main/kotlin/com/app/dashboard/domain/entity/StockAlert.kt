package com.app.dashboard.domain.entity

data class StockAlert(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val minStock: Int
)