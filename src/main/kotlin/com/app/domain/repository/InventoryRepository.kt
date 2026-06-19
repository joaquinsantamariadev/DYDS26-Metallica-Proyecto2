package com.app.domain.repository

import com.app.domain.entity.Product

interface InventoryRepository {
    suspend fun getProducts(): List<Product>
    suspend fun insertProduct(product: Product)
}
