package com.app.reports.domain.repository

import com.app.reports.domain.entity.MarginEntry
import com.app.reports.domain.entity.ProductRotationEntry
import com.app.reports.domain.entity.ReportFilters
import com.app.reports.domain.entity.RevenueSummary
import com.app.reports.domain.entity.TransactionHistoryEntry

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