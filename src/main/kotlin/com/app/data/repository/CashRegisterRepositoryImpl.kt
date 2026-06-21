package com.app.data.repository

import com.app.data.local.sales.CashRegisterLocalDataSource
import com.app.data.mapper.toCashRegisterSession
import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.SessionStatus
import com.app.domain.repository.CashRegisterRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CashRegisterRepositoryImpl(
    private val dataSource: CashRegisterLocalDataSource
) : CashRegisterRepository {
    override suspend fun open(openingAmount: Double): CashRegisterSession = transaction {
        val id = dataSource.insertSession(openingAmount, LocalDateTime.now())
        dataSource.getSessionById(id)!!.toCashRegisterSession()
    }

    override suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession = transaction {
        dataSource.updateSession(
            id = sessionId,
            closingAmount = closingAmount,
            closedAt = LocalDateTime.now(),
            status = SessionStatus.CLOSED.name
        )
        dataSource.getSessionById(sessionId)!!.toCashRegisterSession()
    }

    override suspend fun getActive(): CashRegisterSession? = transaction {
        dataSource.getActiveSession()?.toCashRegisterSession()
    }

    override suspend fun getAll(): List<CashRegisterSession> = transaction {
        dataSource.getAllSessions().map { it.toCashRegisterSession() }
    }
}