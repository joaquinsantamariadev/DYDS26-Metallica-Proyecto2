package com.app.dashboard.data.repository

import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.CashRegisterSessionsTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.common.data.mapper.toExpiryAlert
import com.app.common.data.mapper.toRecentSaleEntry
import com.app.common.data.mapper.toStockAlert
import com.app.dashboard.domain.entity.DashboardMetrics
import com.app.dashboard.domain.entity.ExpiryAlert
import com.app.dashboard.domain.entity.RecentSaleEntry
import com.app.dashboard.domain.entity.StockAlert
import com.app.dashboard.domain.repository.DashboardRepository
import com.app.dashboard.domain.usecase.GetExpiryAlertsUseCaseImpl
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
            val cutoff = today.plusDays(GetExpiryAlertsUseCaseImpl.EXPIRY_ALERT_DAYS.toLong())

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