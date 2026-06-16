package com.app.domain.entity

data class ExchangeRate(
    val currencyPair: String,
    val rate: Double,
    val lastUpdated: Long
)
