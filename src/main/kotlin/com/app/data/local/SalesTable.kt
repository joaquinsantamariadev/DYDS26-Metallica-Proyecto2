package com.app.data.local

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object SalesTable : Table("sales") {
    val id = integer("id").autoIncrement()
    val sessionId = integer("session_id").references(CashRegisterSessionsTable.id)
    val total = double("total")
    val paymentMethod = varchar("payment_method", 50)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}