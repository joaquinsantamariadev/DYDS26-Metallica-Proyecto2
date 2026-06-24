package com.app.settings.domain.usecase

import com.app.settings.domain.entity.ExportFormat
import com.app.settings.domain.repository.ExportRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout

interface ExportDataUseCase {
    suspend operator fun invoke(basePath: String, format: ExportFormat)
}

class ExportDataUseCaseImpl(private val exportRepository: ExportRepository) : ExportDataUseCase {
    override suspend operator fun invoke(basePath: String, format: ExportFormat) = coroutineScope {
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
