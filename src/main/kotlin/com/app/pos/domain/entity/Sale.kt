package com.app.pos.domain.entity

import com.app.pos.domain.entity.PaymentMethod
import java.time.LocalDateTime

data class Sale(
    val id: Int? = null,
    val sessionId: Int,
    val items: List<SaleItem>,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val createdAt: LocalDateTime
)