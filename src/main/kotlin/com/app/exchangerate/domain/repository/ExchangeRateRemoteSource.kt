package com.app.exchangerate.domain.repository

import com.app.exchangerate.domain.entity.ExchangeRate

interface ExchangeRateRemoteSource {
    suspend fun getBlueRate(): ExchangeRate
}
