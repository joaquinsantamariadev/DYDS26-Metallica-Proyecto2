package com.app.pos.data.local

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object CashRegisterSessionsTable : Table("cash_register_sessions") {
    val id = integer("id").autoIncrement()
    val openingAmount = double("opening_amount")
    val closingAmount = double("closing_amount").nullable()
    val openedAt = datetime("opened_at")
    val closedAt = datetime("closed_at").nullable()
    val status = varchar("status", 20)
    override val primaryKey = PrimaryKey(id)
}