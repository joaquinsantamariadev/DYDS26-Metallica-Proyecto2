package com.app.settings.domain.repository

import com.app.settings.domain.entity.ExportFormat

interface ExportRepository {
    suspend fun exportProducts(filePath: String, format: ExportFormat)
    suspend fun exportSales(filePath: String, format: ExportFormat)
    suspend fun exportCategories(filePath: String, format: ExportFormat)
}
