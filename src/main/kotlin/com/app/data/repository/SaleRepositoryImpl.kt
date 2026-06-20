package com.app.data.repository

import com.app.data.local.SaleLocalDataSource
import com.app.data.local.SalesTable
import com.app.data.mapper.SaleMapper
import com.app.domain.entity.Sale
import com.app.domain.repository.SaleRepository
import org.jetbrains.exposed.sql.transactions.transaction

class SaleRepositoryImpl(
    private val dataSource: SaleLocalDataSource,
    private val mapper: SaleMapper
) : SaleRepository {
    override suspend fun save(sale: Sale): Sale {
        val saleId = transaction {
            val id = dataSource.insertSale(
                sessionId = sale.sessionId,
                total = sale.total,
                paymentMethod = sale.paymentMethod.name,
                createdAt = sale.createdAt
            )
            sale.items.forEach { item ->
                dataSource.insertSaleItem(
                    saleId = id,
                    productId = item.productId,
                    productName = item.productName,
                    unitPrice = item.unitPrice,
                    quantity = item.quantity,
                    subtotal = item.subtotal
                )
            }
            id
        }
        return sale.copy(id = saleId)
    }

    override suspend fun getById(id: Int): Sale? = transaction {
        val saleRow = dataSource.getSaleById(id) ?: return@transaction null
        val itemRows = dataSource.getSaleItemsBySaleId(id)
        mapper.map(saleRow, itemRows)
    }

    override suspend fun getAll(): List<Sale> = transaction {
        dataSource.getAllSales().map { row ->
            val itemRows = dataSource.getSaleItemsBySaleId(row[SalesTable.id])
            mapper.map(row, itemRows)
        }
    }

    override suspend fun getBySessionId(sessionId: Int): List<Sale> = transaction {
        dataSource.getSalesBySessionId(sessionId).map { row ->
            val itemRows = dataSource.getSaleItemsBySaleId(row[SalesTable.id])
            mapper.map(row, itemRows)
        }
    }
}