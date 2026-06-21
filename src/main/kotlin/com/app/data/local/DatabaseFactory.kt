package com.app.data.local

import com.app.data.local.inventory.CategoryTable
import com.app.data.local.inventory.ExchangeRateTable
import com.app.data.local.inventory.ProductTable
import com.app.data.local.sales.CashRegisterSessionsTable
import com.app.data.local.sales.SaleItemsTable
import com.app.data.local.sales.SalesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        val dbFile = File("stock_control.db")
        Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )
        
        transaction {
            SchemaUtils.create(
                CategoryTable, ProductTable, ExchangeRateTable, CashRegisterSessionsTable,
                SalesTable, SaleItemsTable
            )
        }
    }
}
