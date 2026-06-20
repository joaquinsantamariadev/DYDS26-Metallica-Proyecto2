package com.app.domain.repository

import com.app.domain.entity.Product

interface InventoryRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Int)
}