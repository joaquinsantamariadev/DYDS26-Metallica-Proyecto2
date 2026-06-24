package com.app.common.data.mapper

import com.app.exchangerate.data.dolarapi.ExchangeRateResponse
import com.app.inventory.data.local.CategoryTable
import com.app.inventory.data.local.ExchangeRateTable
import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.CashRegisterSessionsTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.settings.data.local.SettingsTable
import com.app.inventory.domain.entity.Category
import com.app.inventory.domain.entity.Product
import com.app.exchangerate.domain.entity.ExchangeRate
import com.app.pos.domain.entity.CashRegisterSession
import com.app.pos.domain.entity.PaymentMethod
import com.app.pos.domain.entity.SessionStatus
import com.app.pos.domain.entity.Sale
import com.app.pos.domain.entity.SaleItem
import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings
import org.jetbrains.exposed.sql.ResultRow
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.entity.StockAlert
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

fun ResultRow.toProductCsvMap() = mapOf(
    "id" to this[ProductTable.id].toString(),
    "barcode" to (this[ProductTable.barcode] ?: ""),
    "name" to this[ProductTable.name].replace(",", ""),
    "categoryId" to (this[ProductTable.categoryId]?.toString() ?: ""),
    "price" to this[ProductTable.price].toString(),
    "cost" to this[ProductTable.cost].toString(),
    "stock" to this[ProductTable.stock].toString(),
    "minStock" to this[ProductTable.minStock].toString(),
    "imageUrl" to (this[ProductTable.imageUrl] ?: ""),
    "expiryDate" to (this[ProductTable.expiryDate]?.toString() ?: "")
)

fun ResultRow.toSaleCsvMap() = mapOf(
    "id" to this[SalesTable.id].toString(),
    "sessionId" to this[SalesTable.sessionId].toString(),
    "total" to this[SalesTable.total].toString(),
    "paymentMethod" to this[SalesTable.paymentMethod].toString(),
    "createdAt" to this[SalesTable.createdAt].toString()
)

fun ResultRow.toCategoryCsvMap() = mapOf(
    "id" to this[CategoryTable.id].toString(),
    "name" to this[CategoryTable.name].replace(",", "")
)

fun Iterable<ResultRow>.toSettingsMap() = associate {
    it[SettingsTable.key] to it[SettingsTable.value]
}

fun Map<String, String>.toStoreSettings() = StoreSettings(
    storeName = this["store_name"] ?: "",
    address = this["address"] ?: "",
    phone = this["phone"] ?: "",
    currency = this["currency"] ?: "ARS",
    logoPath = this["logo_path"] ?: ""
)

fun Map<String, String>.toSystemSettings() = SystemSettings(
    defaultLowStockThreshold = this["default_low_stock_threshold"]?.toIntOrNull() ?: 5,
    expiryAlertDays = this["expiry_alert_days"]?.toIntOrNull() ?: 7,
    historyPageSize = this["history_page_size"]?.toIntOrNull() ?: 50,
    rotationTopN = this["rotation_top_n"]?.toIntOrNull() ?: 20,
    isDarkMode = this["is_dark_mode"]?.toBooleanStrictOrNull() ?: false
)

fun StoreSettings.toKeyValuePairs() = mapOf(
    "store_name" to storeName,
    "address" to address,
    "phone" to phone,
    "currency" to currency,
    "logo_path" to logoPath
)

fun SystemSettings.toKeyValuePairs() = mapOf(
    "default_low_stock_threshold" to defaultLowStockThreshold.toString(),
    "expiry_alert_days" to expiryAlertDays.toString(),
    "history_page_size" to historyPageSize.toString(),
    "rotation_top_n" to rotationTopN.toString(),
    "is_dark_mode" to isDarkMode.toString()
)

fun ExchangeRateResponse.toExchangeRate(): ExchangeRate =
    ExchangeRate(
        currencyPair = "USD/ARS",
        rate = sell,
        lastUpdated = System.currentTimeMillis()
    )