package com.app.domain.repository

import com.app.domain.entity.CashRegisterSession

interface CashRegisterRepository {
    suspend fun open(openingAmount: Double): CashRegisterSession
    suspend fun close(sessionId: Int, closingAmount: Double): CashRegisterSession
    suspend fun getActive(): CashRegisterSession?
    suspend fun getAll(): List<CashRegisterSession>
}