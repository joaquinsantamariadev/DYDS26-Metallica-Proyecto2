package com.app.inventory.data.local

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object ProductTable : Table("products") {
    val id = integer("id").autoIncrement()
    val barcode = varchar("barcode", 50).nullable()
    val name = varchar("name", 255)
    val categoryId = integer("category_id").references(CategoryTable.id).nullable()
    val price = double("price")
    val cost = double("cost")
    val stock = integer("stock")
    val minStock = integer("min_stock").default(0)
    val imageUrl = varchar("image_url", 1024).nullable()
    val expiryDate = date("expiry_date").nullable()

    override val primaryKey = PrimaryKey(id)
}