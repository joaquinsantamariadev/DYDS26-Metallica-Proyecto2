package com.app.data.local

import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

interface SaleLocalDataSource {
    fun insertSale(sessionId: Int, total: Double, paymentMethod: String, createdAt: LocalDateTime): Int
    fun insertSaleItem(saleId: Int, productId: Int, productName: String, unitPrice: Double, quantity: Int, subtotal: Double)
    fun getSaleById(id: Int): ResultRow?
    fun getSaleItemsBySaleId(saleId: Int): List<ResultRow>
    fun getAllSales(): List<ResultRow>
    fun getSalesBySessionId(sessionId: Int): List<ResultRow>
}