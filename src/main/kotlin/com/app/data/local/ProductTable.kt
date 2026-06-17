package com.app.data.local

import org.jetbrains.exposed.sql.Table

object ProductTable : Table("products") {
    val id = integer("id").autoIncrement()
    val barcode = varchar("barcode", 50).nullable()
    val name = varchar("name", 255)
    val categoryId = integer("category_id").references(CategoryTable.id).nullable()
    val price = double("price")
    val cost = double("cost")
    val stock = integer("stock")

    override val primaryKey = PrimaryKey(id)
}
