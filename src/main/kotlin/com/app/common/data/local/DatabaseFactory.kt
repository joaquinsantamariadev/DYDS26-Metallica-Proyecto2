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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
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
            seedFakeSales()
        }
    }

    private fun seedFakeSales() {
        val existingSales = SalesTable.selectAll().count()
        if (existingSales > 20L) return

        CashRegisterSessionsTable.insert {
            it[openingAmount] = 1000.0
            it[openedAt] = java.time.LocalDateTime.now().minusMonths(6)
            it[status] = "CLOSED"
        }

        val sessionRow = CashRegisterSessionsTable.selectAll().lastOrNull() ?: return
        val sId = sessionRow[CashRegisterSessionsTable.id]

        val random = java.util.Random()
        for (i in 0..5) {
            val monthDate = java.time.LocalDateTime.now().minusMonths((5 - i).toLong())
            val salesCount = random.nextInt(20) + 10
            for (j in 0..salesCount) {
                SalesTable.insert {
                    it[sessionId] = sId
                    it[total] = (random.nextInt(5000) + 500).toDouble()
                    it[paymentMethod] = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA").random()
                    it[createdAt] = monthDate.withDayOfMonth(random.nextInt(25) + 1)
                }
            }
        }
    }
}
