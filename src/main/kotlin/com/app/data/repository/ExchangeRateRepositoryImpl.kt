package com.app.data.repository

import com.app.data.local.ExchangeRateTable
import com.app.data.mapper.toExchangeRate
import com.app.domain.entity.ExchangeRate
import com.app.domain.repository.ExchangeRateRepository
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExchangeRateRepositoryImpl : ExchangeRateRepository {
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
}
