package com.app.exchangerate.data.dolarapi

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

import com.app.exchangerate.domain.repository.ExchangeRateRemoteSource
import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.common.data.mapper.toExchangeRate

class ExchangeRateRemoteDataSource(private val client: HttpClient) : ExchangeRateRemoteSource {

    companion object {
        private const val BLUE_RATE_URL = "https://dolarapi.com/v1/dolares/blue"
    }

    override suspend fun getBlueRate(): ExchangeRate =
        client.get(BLUE_RATE_URL).body<ExchangeRateResponse>().toExchangeRate()
}