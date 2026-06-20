package com.app.domain.usecase

import com.app.domain.repository.ProductExternalSource
import com.app.domain.entity.Product
import com.app.domain.repository.InventoryRepository

class ScanProductUseCase(
    private val repository: InventoryRepository,
    private val externalSource: ProductExternalSource
) {
    suspend operator fun invoke(barcode: String): Product? {
        val localProduct = repository.getProductByBarcode(barcode)
        if (localProduct != null) return localProduct

        val remoteProduct = externalSource.fetchByBarcode(barcode)
        if (remoteProduct != null) {
            repository.insertProduct(remoteProduct)
            return repository.getProductByBarcode(barcode) ?: remoteProduct
        }

        return null
    }
}