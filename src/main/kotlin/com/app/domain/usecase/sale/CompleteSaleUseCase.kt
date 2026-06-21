package com.app.domain.usecase.sale

import com.app.domain.entity.PaymentMethod
import com.app.domain.entity.sale.Sale
import com.app.domain.entity.sale.SaleItem
import com.app.domain.repository.InventoryRepository
import com.app.domain.repository.SaleRepository
import com.app.domain.usecase.cart.ValidateCartStockUseCase
import java.time.LocalDateTime

class CompleteSaleUseCase(
    private val validateCartStockUseCase: ValidateCartStockUseCase,
    private val inventoryRepository: InventoryRepository,
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(
        sessionId: Int,
        items: List<SaleItem>,
        paymentMethod: PaymentMethod
    ): Result<Sale> {
        val validation = validateCartStockUseCase(items.map { it.productId to it.quantity })
        if (validation.isFailure) return Result.failure(validation.exceptionOrNull()!!)

        items.forEach { item ->
            val product = inventoryRepository.getProductById(item.productId)!!
            inventoryRepository.updateStock(item.productId, product.stock - item.quantity)
        }

        val sale = Sale(
            sessionId = sessionId,
            items = items,
            total = items.sumOf { it.subtotal },
            paymentMethod = paymentMethod,
            createdAt = LocalDateTime.now()
        )
        return runCatching { saleRepository.save(sale) }
    }
}