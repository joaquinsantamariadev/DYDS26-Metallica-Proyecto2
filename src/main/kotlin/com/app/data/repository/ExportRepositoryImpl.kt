package com.app.data.repository

import com.app.data.local.inventory.CategoryTable
import com.app.data.local.inventory.ProductTable
import com.app.data.local.sales.SalesTable
import com.app.domain.entity.settings.ExportFormat
import com.app.domain.repository.ExportRepository
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedWriter
import java.io.File

class ExportRepositoryImpl : ExportRepository {
    override suspend fun exportProducts(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val products = transaction {
            ProductTable.selectAll().toList()
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,barcode,name,category_id,price,cost,stock,min_stock,image_url,expiry_date")
            writer.newLine()
            products.forEach { row ->
                writer.write("${row[ProductTable.id]},${row[ProductTable.barcode] ?: ""},${row[ProductTable.name].replace(",", "")},${row[ProductTable.categoryId] ?: ""},${row[ProductTable.price]},${row[ProductTable.cost]},${row[ProductTable.stock]},${row[ProductTable.minStock]},${row[ProductTable.imageUrl] ?: ""},${row[ProductTable.expiryDate] ?: ""}")
                writer.newLine()
            }
        }
    }

    override suspend fun exportSales(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val sales = transaction {
            SalesTable.selectAll().toList()
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,session_id,total,payment_method,created_at")
            writer.newLine()
            sales.forEach { row ->
                writer.write("${row[SalesTable.id]},${row[SalesTable.sessionId]},${row[SalesTable.total]},${row[SalesTable.paymentMethod]},${row[SalesTable.createdAt]}")
                writer.newLine()
            }
        }
    }

    override suspend fun exportCategories(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val categories = transaction {
            CategoryTable.selectAll().toList()
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,name")
            writer.newLine()
            categories.forEach { row ->
                writer.write("${row[CategoryTable.id]},${row[CategoryTable.name].replace(",", "")}")
                writer.newLine()
            }
        }
    }
}
