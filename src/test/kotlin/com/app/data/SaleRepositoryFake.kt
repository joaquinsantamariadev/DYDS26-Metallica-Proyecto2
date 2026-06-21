package com.app.fakes

import com.app.domain.entity.Sale
import com.app.domain.repository.SaleRepository

class SaleRepositoryFake : SaleRepository {
    var savedSale: Sale? = null
    var shouldThrowError = false

    override suspend fun save(sale: Sale): Sale {
        if (shouldThrowError) throw Exception("Sale save error")
        return sale.copy(id = 1).also { savedSale = it }
    }

    override suspend fun getById(id: Int): Sale? =
        savedSale?.takeIf { it.id == id }

    override suspend fun getAll(): List<Sale> =
        listOfNotNull(savedSale)

    override suspend fun getBySessionId(sessionId: Int): List<Sale> =
        listOfNotNull(savedSale?.takeIf { it.sessionId == sessionId })
}