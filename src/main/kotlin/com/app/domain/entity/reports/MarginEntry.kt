package com.app.domain.entity.reports

data class MarginEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val costPrice: Double,
    val salePrice: Double,
    val grossMargin: Double,
    val grossMarginPercent: Double
)