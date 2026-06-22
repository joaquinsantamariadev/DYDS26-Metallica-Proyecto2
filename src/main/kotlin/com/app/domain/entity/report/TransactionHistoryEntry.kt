package com.app.domain.entity.report

import java.time.LocalDateTime

data class TransactionHistoryEntry(
    val saleId: Long,
    val dateTime: LocalDateTime,
    val items: List<TransactionItemDetail>,
    val total: Double
)