package com.app.domain.entity

import java.time.LocalDateTime

data class CashRegisterSession(
    val id: Int? = null,
    val openingAmount: Double,
    val closingAmount: Double? = null,
    val openedAt: LocalDateTime,
    val closedAt: LocalDateTime? = null,
    val status: SessionStatus
)