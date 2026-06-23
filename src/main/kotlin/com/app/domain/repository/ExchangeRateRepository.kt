package com.app.domain.repository

import com.app.domain.entity.ExchangeRate

interface ExchangeRateRepository {
    suspend fun getLocalRate(currencyPair: String): ExchangeRate?
    suspend fun saveRate(rate: ExchangeRate)
    suspend fun getBlueRate(): ExchangeRate
}
