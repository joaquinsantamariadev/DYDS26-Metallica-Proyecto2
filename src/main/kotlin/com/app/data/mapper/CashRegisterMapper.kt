package com.app.data.mapper

import com.app.data.local.CashRegisterSessionsTable
import com.app.domain.entity.CashRegisterSession
import com.app.domain.entity.SessionStatus
import org.jetbrains.exposed.sql.ResultRow

class CashRegisterMapper {
    fun map(row: ResultRow): CashRegisterSession = CashRegisterSession(
        id = row[CashRegisterSessionsTable.id],
        openingAmount = row[CashRegisterSessionsTable.openingAmount],
        closingAmount = row[CashRegisterSessionsTable.closingAmount],
        openedAt = row[CashRegisterSessionsTable.openedAt],
        closedAt = row[CashRegisterSessionsTable.closedAt],
        status = SessionStatus.valueOf(row[CashRegisterSessionsTable.status])
    )
}