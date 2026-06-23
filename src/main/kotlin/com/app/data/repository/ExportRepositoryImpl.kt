package com.app.data.repository

import com.app.data.local.inventory.CategoryTable
import com.app.data.local.inventory.ProductTable
import com.app.data.local.sales.SalesTable
import com.app.data.mapper.*
import com.app.domain.entity.settings.ExportFormat
import com.app.domain.repository.ExportRepository
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedWriter
import java.io.File

class ExportRepositoryImpl : ExportRepository {
    override suspend fun exportProducts(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val productData = transaction {
            ProductTable.selectAll().map { it.toProductCsvMap() }
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,barcode,name,category_id,price,cost,stock,min_stock,image_url,expiry_date")
            writer.newLine()
            productData.forEach { data ->
                writer.write("${data["id"]},${data["barcode"]},${data["name"]},${data["categoryId"]},${data["price"]},${data["cost"]},${data["stock"]},${data["minStock"]},${data["imageUrl"]},${data["expiryDate"]}")
                writer.newLine()
            }
        }
    }

    override suspend fun exportSales(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val salesData = transaction {
            SalesTable.selectAll().map { it.toSaleCsvMap() }
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,session_id,total,payment_method,created_at")
            writer.newLine()
            salesData.forEach { data ->
                writer.write("${data["id"]},${data["sessionId"]},${data["total"]},${data["paymentMethod"]},${data["createdAt"]}")
                writer.newLine()
            }
        }
    }

    override suspend fun exportCategories(filePath: String, format: ExportFormat) {
        if (format != ExportFormat.CSV) throw IllegalArgumentException("Format $format not supported")

        val categoriesData = transaction {
            CategoryTable.selectAll().map { it.toCategoryCsvMap() }
        }

        File(filePath).bufferedWriter().use { writer ->
            writer.write("id,name")
            writer.newLine()
            categoriesData.forEach { data ->
                writer.write("${data["id"]},${data["name"]}")
                writer.newLine()
            }
        }
    }
}
