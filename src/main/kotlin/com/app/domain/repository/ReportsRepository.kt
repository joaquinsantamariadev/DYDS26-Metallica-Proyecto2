package com.app.domain.repository

import com.app.domain.entity.report.MarginEntry
import com.app.domain.entity.report.ProductRotationEntry
import com.app.domain.entity.report.ReportFilters
import com.app.domain.entity.report.RevenueSummary
import com.app.domain.entity.report.TransactionHistoryEntry

interface ReportsRepository {
    suspend fun getTransactionHistory(
        filters: ReportFilters,
        limit: Int,
        offset: Int
    ): List<TransactionHistoryEntry>

    suspend fun getRevenueSummary(filters: ReportFilters): RevenueSummary

    suspend fun getProductRotation(
        filters: ReportFilters,
        topN: Int
    ): List<ProductRotationEntry>

    suspend fun getMargins(): List<MarginEntry>
}