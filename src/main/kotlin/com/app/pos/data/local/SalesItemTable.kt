package com.app.pos.data.local

import org.jetbrains.exposed.sql.Table

object SaleItemsTable : Table("sale_items") {
    val id = integer("id").autoIncrement()
    val saleId = integer("sale_id").references(SalesTable.id)
    val productId = integer("product_id")
    val productName = varchar("product_name", 255)
    val unitPrice = double("unit_price")
    val quantity = integer("quantity")
    val subtotal = double("subtotal")
    override val primaryKey = PrimaryKey(id)
}