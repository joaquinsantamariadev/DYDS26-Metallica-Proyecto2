package com.app.data.repository

import com.app.data.local.inventory.ProductTable
import com.app.data.local.sales.CashRegisterSessionsTable
import com.app.data.local.sales.SaleItemsTable
import com.app.data.local.sales.SalesTable
import com.app.data.mapper.toExpiryAlert
import com.app.data.mapper.toRecentSaleEntry
import com.app.data.mapper.toStockAlert
import com.app.domain.entity.dashboard.DashboardMetrics
import com.app.domain.entity.dashboard.ExpiryAlert
import com.app.domain.entity.dashboard.RecentSaleEntry
import com.app.domain.entity.dashboard.StockAlert
import com.app.domain.repository.DashboardRepository
import com.app.domain.usecase.dashboard.GetExpiryAlertsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalTime

class DashboardRepositoryImpl : DashboardRepository {

    override suspend fun getMetrics(): DashboardMetrics = withContext(Dispatchers.IO) {
        transaction {
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay()
            val endOfDay = today.atTime(LocalTime.MAX)
            val cutoff = today.plusDays(GetExpiryAlertsUseCase.EXPIRY_ALERT_DAYS.toLong())

            val products = ProductTable.selectAll().toList()
            val todaySales = SalesTable.selectAll()
                .where { SalesTable.createdAt.between(startOfDay, endOfDay) }
                .toList()
            val hasActiveSession = CashRegisterSessionsTable.selectAll()
                .where { CashRegisterSessionsTable.status eq "OPEN" }
                .count() > 0

            DashboardMetrics(
                totalProducts = products.size,
                inventoryValue = products.sumOf { it[ProductTable.stock] * it[ProductTable.cost] },
                salesToday = todaySales.size,
                revenueToday = todaySales.sumOf { it[SalesTable.total] },
                lowStockCount = products.count { it[ProductTable.stock] <= it[ProductTable.minStock] },
                nearExpiryCount = products.count { row ->
                    val expiry = row[ProductTable.expiryDate]
                    expiry != null && !expiry.isBefore(today) && !expiry.isAfter(cutoff)
                },
                hasActiveSession = hasActiveSession
            )
        }
    }

    override suspend fun getLowStockAlerts(): List<StockAlert> = withContext(Dispatchers.IO) {
        transaction {
            ProductTable.selectAll()
                .toList()
                .filter { it[ProductTable.stock] <= it[ProductTable.minStock] }
                .map { it.toStockAlert() }
        }
    }

    override suspend fun getExpiryAlerts(withinDays: Int): List<ExpiryAlert> = withContext(Dispatchers.IO) {
        transaction {
            val today = LocalDate.now()
            val cutoff = today.plusDays(withinDays.toLong())
            ProductTable.selectAll()
                .toList()
                .filter { row ->
                    val expiry = row[ProductTable.expiryDate]
                    expiry != null && !expiry.isBefore(today) && !expiry.isAfter(cutoff)
                }
                .map { it.toExpiryAlert() }
        }
    }

    override suspend fun getRecentSales(limit: Int): List<RecentSaleEntry> = withContext(Dispatchers.IO) {
        transaction {
            SalesTable.selectAll()
                .orderBy(SalesTable.createdAt, SortOrder.DESC)
                .limit(limit)
                .map { saleRow ->
                    val itemCount = SaleItemsTable.selectAll()
                        .where { SaleItemsTable.saleId eq saleRow[SalesTable.id] }
                        .count()
                        .toInt()
                    saleRow.toRecentSaleEntry(itemCount)
                }
        }
    }
}