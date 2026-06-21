package com.app.data.local.sales

import com.app.domain.entity.SessionStatus
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class CashRegisterLocalDataSourceImpl : CashRegisterLocalDataSource {
    override fun insertSession(openingAmount: Double, openedAt: LocalDateTime): Int =
        CashRegisterSessionsTable.insert {
            it[CashRegisterSessionsTable.openingAmount] = openingAmount
            it[CashRegisterSessionsTable.openedAt] = openedAt
            it[CashRegisterSessionsTable.status] = SessionStatus.OPEN.name
        }[CashRegisterSessionsTable.id]

    override fun updateSession(id: Int, closingAmount: Double, closedAt: LocalDateTime, status: String) {
        CashRegisterSessionsTable.update({ CashRegisterSessionsTable.id eq id }) {
            it[CashRegisterSessionsTable.closingAmount] = closingAmount
            it[CashRegisterSessionsTable.closedAt] = closedAt
            it[CashRegisterSessionsTable.status] = status
        }
    }

    override fun getSessionById(id: Int): ResultRow? =
        CashRegisterSessionsTable.selectAll()
            .where { CashRegisterSessionsTable.id eq id }
            .singleOrNull()

    override fun getActiveSession(): ResultRow? =
        CashRegisterSessionsTable.selectAll()
            .where { CashRegisterSessionsTable.status eq SessionStatus.OPEN.name }
            .singleOrNull()

    override fun getAllSessions(): List<ResultRow> =
        CashRegisterSessionsTable.selectAll().toList()
}