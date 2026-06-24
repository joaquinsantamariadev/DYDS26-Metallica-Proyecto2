package com.app.exchangerate.data.repository

import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.exchangerate.domain.repository.ExchangeRateRepository

class ExchangeRateRepositoryFake : ExchangeRateRepository {

    var shouldThrowError = false
    var cachedRate: ExchangeRate? = null
    var blueRate = ExchangeRate(currencyPair = "USD/ARS", rate = 1020.0, lastUpdated = 0L)

    override suspend fun getLocalRate(currencyPair: String): ExchangeRate? = cachedRate

    override suspend fun saveRate(rate: ExchangeRate) {
        cachedRate = rate
    }

    override suspend fun getBlueRate(): ExchangeRate {
        if (shouldThrowError) throw Exception("exchange rate error")
        return blueRate
    }
}