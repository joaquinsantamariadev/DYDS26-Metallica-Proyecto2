package com.app.domain.repository

import com.app.domain.entity.Product

interface InventoryRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: Int)
    suspend fun getProductById(id: Int): Product?
    suspend fun updateStock(productId: Int, newStock: Int)
}