package com.app.data

import com.app.domain.entity.report.*
import com.app.domain.repository.ReportsRepository

class ReportsRepositoryFake : ReportsRepository {
    var transactionHistoryResult: List<TransactionHistoryEntry> = emptyList()
    var revenueSummaryResult: RevenueSummary = RevenueSummary(0.0, 0, 0.0, emptyList())
    var productRotationResult: List<ProductRotationEntry> = emptyList()
    var marginsResult: List<MarginEntry> = emptyList()
    var shouldThrowError = false

    var capturedHistoryFilters: ReportFilters? = null
    var capturedRevenueFilters: ReportFilters? = null
    var capturedRotationFilters: ReportFilters? = null
    var capturedLimit: Int? = null
    var capturedOffset: Int? = null
    var capturedTopN: Int? = null

    override suspend fun getTransactionHistory(
        filters: ReportFilters,
        limit: Int,
        offset: Int
    ): List<TransactionHistoryEntry> {
        if (shouldThrowError) throw Exception("transaction history error")
        capturedHistoryFilters = filters
        capturedLimit = limit
        capturedOffset = offset
        return transactionHistoryResult
    }

    override suspend fun getRevenueSummary(filters: ReportFilters): RevenueSummary {
        if (shouldThrowError) throw Exception("revenue summary error")
        capturedRevenueFilters = filters
        return revenueSummaryResult
    }

    override suspend fun getProductRotation(
        filters: ReportFilters,
        topN: Int
    ): List<ProductRotationEntry> {
        if (shouldThrowError) throw Exception("product rotation error")
        capturedRotationFilters = filters
        capturedTopN = topN
        return productRotationResult
    }

    override suspend fun getMargins(): List<MarginEntry> {
        if (shouldThrowError) throw Exception("margins error")
        return marginsResult
    }
}
