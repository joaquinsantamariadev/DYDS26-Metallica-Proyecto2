package com.app.data

import com.app.domain.entity.Product
import com.app.domain.repository.InventoryRepository

class InventoryRepositoryFake : InventoryRepository {
    val productsList = mutableListOf<Product>()
    var insertProductCalled = false
    var updateProductCalled = false
    var deleteProductCalled = false
    var updateStockCalled = false
    val stockUpdated = mutableMapOf<Int, Int>()

    override suspend fun getProducts(): List<Product> = productsList

    override suspend fun getProductByBarcode(barcode: String): Product? =
        productsList.firstOrNull { it.barcode == barcode }

    override suspend fun getProductById(id: Int): Product? =
        productsList.firstOrNull { it.id == id }

    override suspend fun insertProduct(product: Product) {
        insertProductCalled = true
        productsList.add(product)
    }

    override suspend fun updateProduct(product: Product) {
        updateProductCalled = true
        val index = productsList.indexOfFirst { it.id == product.id }
        if (index >= 0) productsList[index] = product
    }

    override suspend fun deleteProduct(id: Int) {
        deleteProductCalled = true
        productsList.removeAll { it.id == id }
    }

    override suspend fun updateStock(productId: Int, newStock: Int) {
        updateStockCalled = true
        stockUpdated[productId] = newStock
        val index = productsList.indexOfFirst { it.id == productId }
        if (index >= 0) productsList[index] = productsList[index].copy(stock = newStock)
    }
}
