package com.app.pos.data.repository

import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.entity.SessionStatus
import com.app.pos.domain.repository.CashRegisterRepository
import java.time.LocalDateTime

class CashRegisterRepositoryFake : CashRegisterRepository {
    var activeSession: CashRegisterSession? = null
    var shouldThrowError = false
    var closeCalled = false
    private val sessions = mutableListOf<CashRegisterSession>()

    override suspend fun open(openingAmount: Double): CashRegisterSession {
        if (shouldThrowError) throw Exception("Open error")
        val session = CashRegisterSession(
            id = sessions.size + 1,
            openingAmount = openingAmount,
            openedAt = LocalDateTime.now(),
            status = SessionStatus.OPEN
        )
        activeSession = session
        sessions.add(session)
        return session
    }

    override suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession {
        if (shouldThrowError) throw Exception("Close error")
        closeCalled = true
        val closed = activeSession!!.copy(
            closingAmount = closingAmount,
            closedAt = LocalDateTime.now(),
            status = SessionStatus.CLOSED
        )
        activeSession = null
        val index = sessions.indexOfFirst { it.id == sessionId }
        if (index >= 0) sessions[index] = closed
        return closed
    }

    override suspend fun getActive(): CashRegisterSession? = activeSession

    override suspend fun getAll(): List<CashRegisterSession> = sessions.toList()
}