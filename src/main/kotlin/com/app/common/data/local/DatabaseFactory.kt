package com.app.common.data.local

import com.app.inventory.data.local.CategoryTable
import com.app.inventory.data.local.ExchangeRateTable
import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.CashRegisterSessionsTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.settings.data.local.SettingsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        val dbFile = File(System.getProperty("user.dir"), "stock_control.db")
        Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )
        
        transaction {
            SchemaUtils.create(
                CategoryTable, ProductTable, ExchangeRateTable, CashRegisterSessionsTable,
                SalesTable, SaleItemsTable, SettingsTable
            )
        }
    }
}
