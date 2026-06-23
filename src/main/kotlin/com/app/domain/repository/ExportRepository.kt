package com.app.domain.repository

import com.app.domain.entity.settings.ExportFormat

interface ExportRepository {
    suspend fun exportProducts(filePath: String, format: ExportFormat)
    suspend fun exportSales(filePath: String, format: ExportFormat)
    suspend fun exportCategories(filePath: String, format: ExportFormat)
}
