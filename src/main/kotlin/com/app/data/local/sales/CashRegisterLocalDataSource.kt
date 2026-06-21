package com.app.data.local.sales

import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

interface CashRegisterLocalDataSource {
    fun insertSession(openingAmount: Double, openedAt: LocalDateTime): Int
    fun updateSession(id: Int, closingAmount: Double, closedAt: LocalDateTime, status: String)
    fun getSessionById(id: Int): ResultRow?
    fun getActiveSession(): ResultRow?
    fun getAllSessions(): List<ResultRow>
}