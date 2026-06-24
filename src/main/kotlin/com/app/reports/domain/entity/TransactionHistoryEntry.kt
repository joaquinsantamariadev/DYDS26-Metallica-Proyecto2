package com.app.reports.domain.entity

import java.time.LocalDateTime

data class TransactionHistoryEntry(
    val saleId: Long,
    val dateTime: LocalDateTime,
    val items: List<TransactionItemDetail>,
    val total: Double
)