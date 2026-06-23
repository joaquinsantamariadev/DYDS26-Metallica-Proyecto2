package com.app.data.external.dolar

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ExchangeRateRemoteDataSource(private val client: HttpClient) {

    companion object {
        private const val BLUE_RATE_URL = "https://dolarapi.com/v1/dolares/blue"
    }

    suspend fun getBlueRate(): ExchangeRateResponse =
        client.get(BLUE_RATE_URL).body()
}