package com.app.inventory.data.openfoodfacts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OffResponse(
    val code: String? = null,
    val status: Int? = null,
    val product: OffProduct? = null
)

@Serializable
data class OffProduct(
    @SerialName("product_name")
    val productName: String? = null
)
