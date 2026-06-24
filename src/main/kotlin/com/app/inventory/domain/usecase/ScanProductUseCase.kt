package com.app.inventory.domain.usecase

import com.app.inventory.domain.repository.ProductExternalSource
import com.app.inventory.domain.entity.Product
import com.app.inventory.domain.repository.InventoryRepository

interface ScanProductUseCase {
    suspend operator fun invoke(barcode: String): Product?
}

class ScanProductUseCaseImpl(
    private val inventoryRepository: InventoryRepository
) : ScanProductUseCase {
    override suspend operator fun invoke(barcode: String): Product? {
        return inventoryRepository.getProductByBarcode(barcode)
    }
}