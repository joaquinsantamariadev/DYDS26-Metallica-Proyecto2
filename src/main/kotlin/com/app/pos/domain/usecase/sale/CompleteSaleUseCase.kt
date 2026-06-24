package com.app.pos.domain.usecase.sale

import com.app.pos.domain.entity.PaymentMethod
import com.app.pos.domain.entity.Sale
import com.app.pos.domain.entity.SaleItem
import com.app.inventory.domain.repository.InventoryRepository
import com.app.pos.domain.repository.SaleRepository
import com.app.pos.domain.usecase.cart.ValidateCartStockUseCase
import java.time.LocalDateTime

interface CompleteSaleUseCase {
    suspend operator fun invoke(
        sessionId: Int,
        items: List<SaleItem>,
        paymentMethod: PaymentMethod
    ): Result<Sale>
}

class CompleteSaleUseCaseImpl(
    private val validateCartStockUseCase: ValidateCartStockUseCase,
    private val inventoryRepository: InventoryRepository,
    private val saleRepository: SaleRepository
) : CompleteSaleUseCase {
    override suspend operator fun invoke(
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