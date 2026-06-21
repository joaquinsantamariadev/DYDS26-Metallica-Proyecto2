package com.app.data.mapper

import com.app.data.local.inventory.CategoryTable
import com.app.data.local.inventory.ExchangeRateTable
import com.app.data.local.inventory.ProductTable
import com.app.data.local.sales.CashRegisterSessionsTable
import com.app.data.local.sales.SaleItemsTable
import com.app.data.local.sales.SalesTable
import com.app.domain.entity.*
import com.app.domain.entity.sale.Sale
import com.app.domain.entity.sale.SaleItem
import org.jetbrains.exposed.sql.ResultRow
import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.entity.dashboard.StockAlert
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun ResultRow.toCategory() = Category(
    id = this[CategoryTable.id],
    name = this[CategoryTable.name]
)

fun ResultRow.toProduct() = Product(
    id = this[ProductTable.id],
    barcode = this[ProductTable.barcode],
    name = this[ProductTable.name],
    categoryId = this[ProductTable.categoryId],
    price = this[ProductTable.price],
    cost = this[ProductTable.cost],
    stock = this[ProductTable.stock],
    imageUrl = this[ProductTable.imageUrl],
    expiryDate = this[ProductTable.expiryDate]
)

fun ResultRow.toExchangeRate() = ExchangeRate(
    currencyPair = this[ExchangeRateTable.currencyPair],
    rate = this[ExchangeRateTable.rate],
    lastUpdated = this[ExchangeRateTable.lastUpdated]
)

fun ResultRow.toSaleItem() = SaleItem(
    id = this[SaleItemsTable.id],
    productId = this[SaleItemsTable.productId],
    productName = this[SaleItemsTable.productName],
    unitPrice = this[SaleItemsTable.unitPrice],
    quantity = this[SaleItemsTable.quantity],
    subtotal = this[SaleItemsTable.subtotal]
)

fun ResultRow.toSale(itemRows: List<ResultRow>) = Sale(
    id = this[SalesTable.id],
    sessionId = this[SalesTable.sessionId],
    items = itemRows.map { it.toSaleItem() },
    total = this[SalesTable.total],
    paymentMethod = PaymentMethod.valueOf(this[SalesTable.paymentMethod]),
    createdAt = this[SalesTable.createdAt]
)

fun ResultRow.toCashRegisterSession() = CashRegisterSession(
    id = this[CashRegisterSessionsTable.id],
    openingAmount = this[CashRegisterSessionsTable.openingAmount],
    closingAmount = this[CashRegisterSessionsTable.closingAmount],
    openedAt = this[CashRegisterSessionsTable.openedAt],
    closedAt = this[CashRegisterSessionsTable.closedAt],
    status = SessionStatus.valueOf(this[CashRegisterSessionsTable.status])
)

fun ResultRow.toStockAlert() = StockAlert(
    productId = this[ProductTable.id].toLong(),
    productName = this[ProductTable.name],
    currentStock = this[ProductTable.stock],
    minStock = this[ProductTable.minStock]
)

fun ResultRow.toExpiryAlert(): ExpiryAlert {
    val expiry = this[ProductTable.expiryDate]!!
    return ExpiryAlert(
        productId = this[ProductTable.id].toLong(),
        productName = this[ProductTable.name],
        expiryDate = expiry,
        daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), expiry).toInt()
    )
}

fun ResultRow.toRecentSaleEntry(itemCount: Int) = RecentSaleEntry(
    saleId = this[SalesTable.id].toLong(),
    dateTime = this[SalesTable.createdAt],
    itemCount = itemCount,
    total = this[SalesTable.total]
)
