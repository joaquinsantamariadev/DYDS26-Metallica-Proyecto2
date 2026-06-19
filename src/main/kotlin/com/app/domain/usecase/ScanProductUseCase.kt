package com.app.domain.usecase

import com.app.data.external.OpenFoodFactsClient
import com.app.domain.entity.Product
import com.app.domain.repository.InventoryRepository

class ScanProductUseCase(
    private val repository: InventoryRepository,
    private val apiClient: OpenFoodFactsClient
) {
    suspend operator fun invoke(barcode: String): Product? {
        val localProduct = repository.getProductByBarcode(barcode)
        if (localProduct != null) {
            return localProduct
        }

        val remoteProduct = apiClient.fetchProductByBarcode(barcode)
        if (remoteProduct != null) {
            repository.insertProduct(remoteProduct)
            return repository.getProductByBarcode(barcode) ?: remoteProduct
        }

        return null
    }
}
