package com.app.data.mapper

import com.app.data.local.SaleItemsTable
import com.app.domain.entity.SaleItem
import org.jetbrains.exposed.sql.ResultRow

class SaleItemMapper {
    fun map(row: ResultRow): SaleItem = SaleItem(
        id = row[SaleItemsTable.id],
        productId = row[SaleItemsTable.productId],
        productName = row[SaleItemsTable.productName],
        unitPrice = row[SaleItemsTable.unitPrice],
        quantity = row[SaleItemsTable.quantity],
        subtotal = row[SaleItemsTable.subtotal]
    )
}