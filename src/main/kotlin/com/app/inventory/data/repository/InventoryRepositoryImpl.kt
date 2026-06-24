package com.app.inventory.data.repository

import com.app.inventory.data.local.ProductTable
import com.app.common.data.mapper.toProduct
import com.app.inventory.domain.entity.Product
import com.app.inventory.domain.repository.InventoryRepository
import com.app.inventory.domain.repository.ProductExternalSource
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class InventoryRepositoryImpl(
    private val externalSource: ProductExternalSource
) : InventoryRepository {
    override suspend fun getProducts(): List<Product> = transaction {
        ProductTable.selectAll().map { it.toProduct() }
    }

    override suspend fun getProductByBarcode(barcode: String): Product? {
        val local = transaction {
            ProductTable.selectAll()
                .where { ProductTable.barcode eq barcode }
                .map { it.toProduct() }
                .singleOrNull()
        }
        if (local != null) return local

        val remote = externalSource.fetchByBarcode(barcode)
        if (remote != null) {
            insertProduct(remote)
            return remote
        }
        return null
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

    override suspend fun updateProduct(product: Product) {
        transaction {
            ProductTable.update({ ProductTable.id eq product.id!! }) {
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

    override suspend fun deleteProduct(id: Int) {
        transaction {
            ProductTable.deleteWhere { ProductTable.id eq id }
        }
    }

    override suspend fun getProductById(id: Int): Product? = transaction {
        ProductTable.selectAll()
            .where { ProductTable.id eq id }
            .map { it.toProduct() }
            .singleOrNull()
    }

    override suspend fun updateStock(productId: Int, newStock: Int) {
        transaction {
            ProductTable.update({ ProductTable.id eq productId }) {
                it[stock] = newStock
            }
        }
    }
}