package com.app.domain.usecase.cart

import com.app.domain.repository.InventoryRepository

class ValidateCartStockUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(items: List<Pair<Int, Int>>): Result<Unit> {
        val insufficient = items.mapNotNull { (productId, requestedQty) ->
            val product = inventoryRepository.getProductById(productId)
            if (product == null || product.stock < requestedQty)
                product?.name ?: "ID=$productId"
            else null
        }
        return if (insufficient.isEmpty())
            Result.success(Unit)
        else
            Result.failure(IllegalStateException("Stock insuficiente: ${insufficient.joinToString()}"))
    }
}