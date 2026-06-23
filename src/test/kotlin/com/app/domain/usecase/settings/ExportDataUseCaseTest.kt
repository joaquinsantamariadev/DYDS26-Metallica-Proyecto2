package com.app.domain.usecase.settings

import com.app.data.ExportRepositoryFake
import com.app.domain.entity.settings.ExportFormat
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ExportDataUseCaseTest {

    private val repository = ExportRepositoryFake()
    private val useCase = ExportDataUseCase(repository)

    @Test
    fun exportsProductsSalesAndCategoriesWithExpectedPaths() {
        runBlocking {
            val basePath = "C:\\exports\\backup"

            useCase(basePath, ExportFormat.CSV)

            assertTrue(repository.exportProductsCalled)
            assertTrue(repository.exportSalesCalled)
            assertTrue(repository.exportCategoriesCalled)

            assertEquals("${basePath}_products.csv", repository.capturedProductsPath)
            assertEquals("${basePath}_sales.csv", repository.capturedSalesPath)
            assertEquals("${basePath}_categories.csv", repository.capturedCategoriesPath)

            assertEquals(ExportFormat.CSV, repository.capturedProductsFormat)
            assertEquals(ExportFormat.CSV, repository.capturedSalesFormat)
            assertEquals(ExportFormat.CSV, repository.capturedCategoriesFormat)
        }
    }

    @Test
    fun propagatesExceptionWhenAnyExportFails() {
        runBlocking {
            repository.shouldThrowOnSales = true

            assertFailsWith<Exception> {
                useCase("C:\\exports\\backup", ExportFormat.CSV)
            }
        }
    }
}