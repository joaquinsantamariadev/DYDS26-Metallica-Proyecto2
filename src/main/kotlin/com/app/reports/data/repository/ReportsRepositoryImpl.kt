package com.app.reports.data.repository

import com.app.inventory.data.local.CategoryTable
import com.app.inventory.data.local.ProductTable
import com.app.pos.data.local.SaleItemsTable
import com.app.pos.data.local.SalesTable
import com.app.reports.data.mapper.periodLabel
import com.app.reports.data.mapper.toMarginEntry
import com.app.reports.data.mapper.toRevenueSummary
import com.app.reports.data.mapper.toTransactionHistoryEntry
import com.app.reports.data.mapper.toTransactionItemDetail
import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.entity.ProductRotationEntry
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.ReportPeriod
import com.app.reports.domain.entity.RevenueDataPoint
import com.app.reports.domain.entity.RevenueSummary
import com.app.reports.domain.entity.TransactionHistoryEntry
import com.app.reports.domain.repository.ReportsRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalTime

class ReportsRepositoryImpl : ReportsRepository {

    override suspend fun getTransactionHistory(
        filters: ReportFilters,
        limit: Int,
        offset: Int
    ): List<TransactionHistoryEntry> = transaction {
        val from = filters.from.atStartOfDay()
        val to = filters.to.atTime(LocalTime.MAX)

        val saleRows = SalesTable
            .selectAll()
            .where { SalesTable.createdAt.between(from, to) }
            .orderBy(SalesTable.createdAt to SortOrder.DESC)
            .limit(limit).offset(offset.toLong())
            .toList()

        if (saleRows.isEmpty()) return@transaction emptyList()

        val saleIds = saleRows.map { it[SalesTable.id] }
        val itemsBySaleId = SaleItemsTable
            .selectAll()
            .where { SaleItemsTable.saleId inList saleIds }
            .toList()
            .groupBy { it[SaleItemsTable.saleId] }

        saleRows.map { row ->
            val items = itemsBySaleId[row[SalesTable.id]]
                ?.map { it.toTransactionItemDetail() }
                ?: emptyList()
            row.toTransactionHistoryEntry(items)
        }
    }

    override suspend fun getRevenueSummary(filters: ReportFilters): RevenueSummary = transaction {
        val from = filters.from.atStartOfDay()
        val to = filters.to.atTime(LocalTime.MAX)

        val format = when (filters.period) {
            ReportPeriod.DAILY -> "%Y-%m-%d"
            ReportPeriod.WEEKLY -> "%Y-W%W"
            ReportPeriod.MONTHLY -> "%Y-%m"
        }

        val periodExpr = CustomFunction<String>("strftime", VarCharColumnType(), stringLiteral(format), SalesTable.createdAt)
        val revenueSum = SalesTable.total.sum()
        val saleCount = SalesTable.id.count()

        SalesTable
            .select(periodExpr, revenueSum, saleCount)
            .where { SalesTable.createdAt.between(from, to) }
            .groupBy(periodExpr)
            .orderBy(periodExpr to SortOrder.ASC)
            .map { row ->
                RevenueDataPoint(
                    periodLabel = periodLabel(row[periodExpr], filters.period),
                    revenue = row[revenueSum] ?: 0.0,
                    salesCount = row[saleCount].toInt()
                )
            }
            .toRevenueSummary()
    }

    override suspend fun getProductRotation(
        filters: ReportFilters,
        topN: Int
    ): List<ProductRotationEntry> = transaction {
        val from = filters.from.atStartOfDay()
        val to = filters.to.atTime(LocalTime.MAX)

        val unitsSold = SaleItemsTable.quantity.sum()
        val revenueSum = SaleItemsTable.subtotal.sum()

        SaleItemsTable
            .join(SalesTable, JoinType.INNER, SaleItemsTable.saleId, SalesTable.id)
            .join(ProductTable, JoinType.LEFT, SaleItemsTable.productId, ProductTable.id)
            .join(CategoryTable, JoinType.LEFT, ProductTable.categoryId, CategoryTable.id)
            .select(SaleItemsTable.productId, SaleItemsTable.productName, CategoryTable.name, ProductTable.categoryId, unitsSold, revenueSum)
            .where { SalesTable.createdAt.between(from, to) }
            .groupBy(SaleItemsTable.productId, SaleItemsTable.productName)
            .orderBy(unitsSold to SortOrder.DESC)
            .limit(topN)
            .map { row ->
                ProductRotationEntry(
                    productId = row[SaleItemsTable.productId].toLong(),
                    productName = row[SaleItemsTable.productName],
                    categoryName = row[ProductTable.categoryId]
                        ?.let { row[CategoryTable.name] }
                        ?: "Sin categoría",
                    unitsSold = row[unitsSold] ?: 0,
                    revenue = row[revenueSum] ?: 0.0
                )
            }
    }

    override suspend fun getMargins(): List<MarginEntry> = transaction {
        ProductTable
            .join(CategoryTable, JoinType.LEFT, ProductTable.categoryId, CategoryTable.id)
            .select(
                ProductTable.id,
                ProductTable.name,
                ProductTable.categoryId,
                ProductTable.cost,
                ProductTable.price,
                CategoryTable.name
            )
            .map { it.toMarginEntry() }
    }
}