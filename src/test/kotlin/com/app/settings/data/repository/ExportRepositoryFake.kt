package com.app.settings.data.repository

import com.app.settings.domain.entity.ExportFormat
import com.app.settings.domain.repository.ExportRepository

class ExportRepositoryFake : ExportRepository {
    var shouldThrowError = false
    var shouldThrowOnProducts = false
    var shouldThrowOnSales = false
    var shouldThrowOnCategories = false

    var exportProductsCalled = false
    var exportSalesCalled = false
    var exportCategoriesCalled = false

    var capturedProductsPath: String? = null
    var capturedSalesPath: String? = null
    var capturedCategoriesPath: String? = null
    var capturedProductsFormat: ExportFormat? = null
    var capturedSalesFormat: ExportFormat? = null
    var capturedCategoriesFormat: ExportFormat? = null

    override suspend fun exportProducts(filePath: String, format: ExportFormat) {
        if (shouldThrowError || shouldThrowOnProducts) throw Exception("export products error")
        exportProductsCalled = true
        capturedProductsPath = filePath
        capturedProductsFormat = format
    }

    override suspend fun exportSales(filePath: String, format: ExportFormat) {
        if (shouldThrowError || shouldThrowOnSales) throw Exception("export sales error")
        exportSalesCalled = true
        capturedSalesPath = filePath
        capturedSalesFormat = format
    }

    override suspend fun exportCategories(filePath: String, format: ExportFormat) {
        if (shouldThrowError || shouldThrowOnCategories) throw Exception("export categories error")
        exportCategoriesCalled = true
        capturedCategoriesPath = filePath
        capturedCategoriesFormat = format
    }
}
