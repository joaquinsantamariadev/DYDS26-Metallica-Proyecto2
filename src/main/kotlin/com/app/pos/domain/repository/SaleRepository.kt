package com.app.pos.domain.repository

import com.app.pos.domain.entity.Sale

interface SaleRepository {
    suspend fun save(sale: Sale): Sale
    suspend fun getById(id: Int): Sale?
    suspend fun getAll(): List<Sale>
    suspend fun getBySessionId(sessionId: Int): List<Sale>
}