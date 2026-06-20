package com.app.data.local

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime

class SaleLocalDataSourceImpl : SaleLocalDataSource {
    override fun insertSale(sessionId: Int, total: Double, paymentMethod: String, createdAt: LocalDateTime): Int =
        SalesTable.insert {
            it[SalesTable.sessionId] = sessionId
            it[SalesTable.total] = total
            it[SalesTable.paymentMethod] = paymentMethod
            it[SalesTable.createdAt] = createdAt
        }[SalesTable.id]

    override fun insertSaleItem(saleId: Int, productId: Int, productName: String, unitPrice: Double, quantity: Int, subtotal: Double) {
        SaleItemsTable.insert {
            it[SaleItemsTable.saleId] = saleId
            it[SaleItemsTable.productId] = productId
            it[SaleItemsTable.productName] = productName
            it[SaleItemsTable.unitPrice] = unitPrice
            it[SaleItemsTable.quantity] = quantity
            it[SaleItemsTable.subtotal] = subtotal
        }
    }

    override fun getSaleById(id: Int): ResultRow? =
        SalesTable.selectAll()
            .where { SalesTable.id eq id }
            .singleOrNull()

    override fun getSaleItemsBySaleId(saleId: Int): List<ResultRow> =
        SaleItemsTable.selectAll()
            .where { SaleItemsTable.saleId eq saleId }
            .toList()

    override fun getAllSales(): List<ResultRow> =
        SalesTable.selectAll().toList()

    override fun getSalesBySessionId(sessionId: Int): List<ResultRow> =
        SalesTable.selectAll()
            .where { SalesTable.sessionId eq sessionId }
            .toList()
}