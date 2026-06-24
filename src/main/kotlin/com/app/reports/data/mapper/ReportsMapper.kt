package com.app.reports.data.mapper

import com.app.inventory.data.local.CategoryTable
import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.entity.ReportPeriod
import com.app.reports.domain.entity.RevenueDataPoint
import com.app.reports.domain.entity.RevenueSummary
import com.app.reports.domain.entity.TransactionHistoryEntry
import com.app.reports.domain.entity.TransactionItemDetail
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toTransactionItemDetail() = TransactionItemDetail(
    productName = this[SaleItemsTable.productName],
    quantity = this[SaleItemsTable.quantity],
    unitPrice = this[SaleItemsTable.unitPrice],
    subtotal = this[SaleItemsTable.subtotal]
)

fun ResultRow.toTransactionHistoryEntry(items: List<TransactionItemDetail>) = TransactionHistoryEntry(
    saleId = this[SalesTable.id].toLong(),
    dateTime = this[SalesTable.createdAt],
    items = items,
    total = this[SalesTable.total]
)

fun ResultRow.toMarginEntry(): MarginEntry {
    val cost = this[ProductTable.cost]
    val price = this[ProductTable.price]
    val grossMargin = price - cost
    val grossMarginPercent = if (price > 0) (grossMargin / price) * 100 else 0.0
    return MarginEntry(
        productId = this[ProductTable.id].toLong(),
        productName = this[ProductTable.name],
        categoryName = this[ProductTable.categoryId]?.let { this[CategoryTable.name] } ?: "Sin categoría",
        costPrice = cost,
        salePrice = price,
        grossMargin = grossMargin,
        grossMarginPercent = grossMarginPercent
    )
}

fun List<RevenueDataPoint>.toRevenueSummary(): RevenueSummary {
    val totalRevenue = sumOf { it.revenue }
    val totalSales = sumOf { it.salesCount }
    val averageTicket = if (totalSales > 0) totalRevenue / totalSales else 0.0
    return RevenueSummary(
        totalRevenue = totalRevenue,
        totalSales = totalSales,
        averageTicket = averageTicket,
        dataPoints = this
    )
}

fun periodLabel(periodKey: String, period: ReportPeriod): String = when (period) {
    ReportPeriod.DAILY -> {
        val parts = periodKey.split("-")
        "${parts[2]}/${parts[1]}"
    }
    ReportPeriod.WEEKLY -> {
        val week = periodKey.substringAfter("W").trimStart('0').ifEmpty { "0" }
        "Sem $week"
    }
    ReportPeriod.MONTHLY -> {
        val parts = periodKey.split("-")
        val months = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
        "${months[parts[1].toInt() - 1]} ${parts[0]}"
    }
}