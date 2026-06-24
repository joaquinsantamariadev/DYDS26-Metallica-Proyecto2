package com.app.exchangerate.data.dolarapi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    @SerialName("compra") val buy: Double,
    @SerialName("venta") val sell: Double,
    @SerialName("nombre") val name: String
)