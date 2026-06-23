package com.app.domain.usecase.settings

import com.app.domain.entity.settings.ExportFormat
import com.app.domain.repository.ExportRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout

class ExportDataUseCase(private val exportRepository: ExportRepository) {
    suspend operator fun invoke(basePath: String, format: ExportFormat) = coroutineScope {
        withTimeout(TIMEOUT_MS) {
            val productsJob = async { exportRepository.exportProducts("${basePath}_products.csv", format) }
            val salesJob = async { exportRepository.exportSales("${basePath}_sales.csv", format) }
            val categoriesJob = async { exportRepository.exportCategories("${basePath}_categories.csv", format) }

            productsJob.await()
            salesJob.await()
            categoriesJob.await()
        }
    }

    companion object {
        private const val TIMEOUT_MS = 10000L
    }
}
