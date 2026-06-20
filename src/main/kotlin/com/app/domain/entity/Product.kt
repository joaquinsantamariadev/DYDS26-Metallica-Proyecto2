package com.app.domain.entity

import java.time.LocalDate

data class Product(
    val id: Int? = null,
    val barcode: String?,
    val name: String,
    val categoryId: Int?,
    val price: Double,
    val cost: Double,
    val stock: Int,
    val imageUrl: String? = null,
    val expiryDate: LocalDate? = null
)