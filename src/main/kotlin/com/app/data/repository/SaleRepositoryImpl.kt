package com.app.data.repository

import com.app.data.local.sales.SaleItemsTable
import com.app.data.local.sales.SalesTable
import com.app.data.mapper.toSale
import com.app.domain.entity.Sale
import com.app.domain.repository.SaleRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class SaleRepositoryImpl : SaleRepository {
    override suspend fun save(sale: Sale): Sale {
        val saleId = transaction {
            val id = SalesTable.insert {
                it[sessionId] = sale.sessionId
                it[total] = sale.total
                it[paymentMethod] = sale.paymentMethod.name
                it[createdAt] = sale.createdAt
            }[SalesTable.id]

            sale.items.forEach { item ->
                SaleItemsTable.insert {
                    it[saleId] = id
                    it[productId] = item.productId
                    it[productName] = item.productName
                    it[unitPrice] = item.unitPrice
                    it[quantity] = item.quantity
                    it[subtotal] = item.subtotal
                }
            }
            id
        }
        return sale.copy(id = saleId)
    }

    override suspend fun getById(id: Int): Sale? = transaction {
        val saleRow = SalesTable.selectAll()
            .where { SalesTable.id eq id }
            .singleOrNull() ?: return@transaction null
        val itemRows = SaleItemsTable.selectAll()
            .where { SaleItemsTable.saleId eq id }
            .toList()
        saleRow.toSale(itemRows)
    }

    override suspend fun getAll(): List<Sale> = transaction {
        SalesTable.selectAll().map { row ->
            val itemRows = SaleItemsTable.selectAll()
                .where { SaleItemsTable.saleId eq row[SalesTable.id] }
                .toList()
            row.toSale(itemRows)
        }
    }

    override suspend fun getBySessionId(sessionId: Int): List<Sale> = transaction {
        SalesTable.selectAll()
            .where { SalesTable.sessionId eq sessionId }
            .map { row ->
                val itemRows = SaleItemsTable.selectAll()
                    .where { SaleItemsTable.saleId eq row[SalesTable.id] }
                    .toList()
                row.toSale(itemRows)
            }
    }
}