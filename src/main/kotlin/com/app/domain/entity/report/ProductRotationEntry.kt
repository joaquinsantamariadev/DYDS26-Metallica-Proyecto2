package com.app.domain.entity.report

data class ProductRotationEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val unitsSold: Int,
    val revenue: Double
)