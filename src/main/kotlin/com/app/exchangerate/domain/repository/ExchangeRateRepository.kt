package com.app.exchangerate.domain.repository

import com.app.exchangerate.domain.entity.ExchangeRate

interface ExchangeRateRepository {
    suspend fun getLocalRate(currencyPair: String): ExchangeRate?
    suspend fun saveRate(rate: ExchangeRate)
    suspend fun getBlueRate(): ExchangeRate
}
