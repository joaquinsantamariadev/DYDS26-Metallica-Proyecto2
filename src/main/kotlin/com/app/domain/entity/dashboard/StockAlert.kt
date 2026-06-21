package com.app.domain.entity.dashboard

data class StockAlert(
    val productId: Long,
    val productName: String,
    val currentStock: Int,
    val minStock: Int
)