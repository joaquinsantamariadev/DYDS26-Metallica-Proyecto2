package com.app.inventory.data.local

import org.jetbrains.exposed.sql.Table

object ExchangeRateTable : Table("exchange_rates") {
    val currencyPair = varchar("currency_pair", 10)
    val rate = double("rate")
    val lastUpdated = long("last_updated")

    override val primaryKey = PrimaryKey(currencyPair)
}