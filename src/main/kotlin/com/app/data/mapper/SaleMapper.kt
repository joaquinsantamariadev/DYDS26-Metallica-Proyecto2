package com.app.data.mapper

import com.app.data.local.SalesTable
import com.app.domain.entity.PaymentMethod
import com.app.domain.entity.Sale
import org.jetbrains.exposed.sql.ResultRow

class SaleMapper(private val saleItemMapper: SaleItemMapper) {
    fun map(row: ResultRow, itemRows: List<ResultRow>): Sale = Sale(
        id = row[SalesTable.id],
        sessionId = row[SalesTable.sessionId],
        items = itemRows.map { saleItemMapper.map(it) },
        total = row[SalesTable.total],
        paymentMethod = PaymentMethod.valueOf(row[SalesTable.paymentMethod]),
        createdAt = row[SalesTable.createdAt]
    )
}