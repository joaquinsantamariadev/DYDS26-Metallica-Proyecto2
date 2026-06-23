package com.app.domain.entity.settings

data class StoreSettings(
    val storeName: String = "",
    val address: String = "",
    val phone: String = "",
    val currency: String = "ARS",
    val logoPath: String = ""
)
