package com.app.reports.domain.entity

data class MarginEntry(
    val productId: Long,
    val productName: String,
    val categoryName: String,
    val costPrice: Double,
    val salePrice: Double,
    val grossMargin: Double,
    val grossMarginPercent: Double
)