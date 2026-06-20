package com.app.data.repository

import com.app.data.local.CashRegisterLocalDataSource
import com.app.data.mapper.CashRegisterMapper
import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.SessionStatus
import com.app.domain.repository.CashRegisterRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CashRegisterRepositoryImpl(
    private val dataSource: CashRegisterLocalDataSource,
    private val mapper: CashRegisterMapper
) : CashRegisterRepository {
    override suspend fun open(openingAmount: Double): CashRegisterSession = transaction {
        val id = dataSource.insertSession(openingAmount, LocalDateTime.now())
        mapper.map(dataSource.getSessionById(id)!!)
    }

    override suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession = transaction {
        dataSource.updateSession(
            id = sessionId,
            closingAmount = closingAmount,
            closedAt = LocalDateTime.now(),
            status = SessionStatus.CLOSED.name
        )
        mapper.map(dataSource.getSessionById(sessionId)!!)
    }

    override suspend fun getActive(): CashRegisterSession? = transaction {
        dataSource.getActiveSession()?.let { mapper.map(it) }
    }

    override suspend fun getAll(): List<CashRegisterSession> = transaction {
        dataSource.getAllSessions().map { mapper.map(it) }
    }
}