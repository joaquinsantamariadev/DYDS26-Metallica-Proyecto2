package com.app.domain.entity.reports

data class ProductRotationEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val unitsSold: Int,
    val revenue: Double
)