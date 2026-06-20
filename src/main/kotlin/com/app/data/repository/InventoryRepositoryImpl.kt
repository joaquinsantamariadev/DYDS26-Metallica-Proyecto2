package com.app.data.repository

import com.app.data.local.ProductTable
import com.app.data.mapper.toProduct
import com.app.domain.entity.Product
import com.app.domain.repository.InventoryRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class InventoryRepositoryImpl : InventoryRepository {
    override suspend fun getProducts(): List<Product> = transaction {
        ProductTable.selectAll().map { it.toProduct() }
    }

    override suspend fun getProductByBarcode(barcode: String): Product? = transaction {
        ProductTable.selectAll()
            .where { ProductTable.barcode eq barcode }
            .map { it.toProduct() }
            .singleOrNull()
    }

    override suspend fun insertProduct(product: Product) {
        transaction {
            ProductTable.insert {
                it[barcode] = product.barcode
                it[name] = product.name
                it[categoryId] = product.categoryId
                it[price] = product.price
                it[cost] = product.cost
                it[stock] = product.stock
                it[imageUrl] = product.imageUrl
                it[expiryDate] = product.expiryDate
            }
        }
    }
}