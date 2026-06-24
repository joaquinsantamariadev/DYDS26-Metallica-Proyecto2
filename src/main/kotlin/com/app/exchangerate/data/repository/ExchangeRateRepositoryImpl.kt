package com.app.exchangerate.data.repository

import com.app.exchangerate.domain.repository.ExchangeRateRemoteSource
import com.app.inventory.data.local.ExchangeRateTable
import com.app.common.data.mapper.toExchangeRate

import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.exchangerate.domain.repository.ExchangeRateRepository
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExchangeRateRepositoryImpl(
    private val remoteSource: ExchangeRateRemoteSource
) : ExchangeRateRepository {

    override suspend fun getLocalRate(currencyPair: String): ExchangeRate? = transaction {
        ExchangeRateTable
            .selectAll()
            .where { ExchangeRateTable.currencyPair eq currencyPair }
            .map { it.toExchangeRate() }
            .singleOrNull()
    }

    override suspend fun saveRate(rate: ExchangeRate) {
        transaction {
            ExchangeRateTable.replace {
                it[currencyPair] = rate.currencyPair
                it[this.rate] = rate.rate
                it[lastUpdated] = rate.lastUpdated
            }
        }
    }

    override suspend fun getBlueRate(): ExchangeRate {
        return try {
            val fresh = remoteSource.getBlueRate()
            saveRate(fresh)
            fresh
        } catch (e: Exception) {
            getLocalRate("USD/ARS") ?: throw e
        }
    }
}