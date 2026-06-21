package com.app.data.repository

import com.app.data.local.sales.CashRegisterSessionsTable
import com.app.data.mapper.toCashRegisterSession
import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.SessionStatus
import com.app.domain.repository.CashRegisterRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class CashRegisterRepositoryImpl : CashRegisterRepository {
    override suspend fun open(openingAmount: Double): CashRegisterSession = transaction {
        val id = CashRegisterSessionsTable.insert {
            it[CashRegisterSessionsTable.openingAmount] = openingAmount
            it[CashRegisterSessionsTable.openedAt] = LocalDateTime.now()
            it[CashRegisterSessionsTable.status] = SessionStatus.OPEN.name
        }[CashRegisterSessionsTable.id]

        CashRegisterSessionsTable.selectAll()
            .where { CashRegisterSessionsTable.id eq id }
            .single()
            .toCashRegisterSession()
    }

    override suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession = transaction {
        CashRegisterSessionsTable.update({ CashRegisterSessionsTable.id eq sessionId }) {
            it[CashRegisterSessionsTable.closingAmount] = closingAmount
            it[CashRegisterSessionsTable.closedAt] = LocalDateTime.now()
            it[CashRegisterSessionsTable.status] = SessionStatus.CLOSED.name
        }

        CashRegisterSessionsTable.selectAll()
            .where { CashRegisterSessionsTable.id eq sessionId }
            .single()
            .toCashRegisterSession()
    }

    override suspend fun getActive(): CashRegisterSession? = transaction {
        CashRegisterSessionsTable.selectAll()
            .where { CashRegisterSessionsTable.status eq SessionStatus.OPEN.name }
            .singleOrNull()
            ?.toCashRegisterSession()
    }

    override suspend fun getAll(): List<CashRegisterSession> = transaction {
        CashRegisterSessionsTable.selectAll()
            .map { it.toCashRegisterSession() }
    }
}