package com.app.di

import com.app.data.external.OpenFoodFactsClient
import com.app.data.repository.CategoryRepositoryImpl
import com.app.data.repository.ExchangeRateRepositoryImpl
import com.app.data.repository.InventoryRepositoryImpl
import com.app.domain.repository.CategoryRepository
import com.app.domain.repository.ExchangeRateRepository
import com.app.domain.repository.InventoryRepository
import com.app.domain.repository.ProductExternalSource
import com.app.domain.usecase.ScanProductUseCase
import com.app.presentation.inventory.CategoryViewModel
import com.app.presentation.inventory.InventoryViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    single<InventoryRepository> { InventoryRepositoryImpl() }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl() }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<ProductExternalSource> { OpenFoodFactsClient(get()) }

    factory { ScanProductUseCase(get(), get()) }
    factory { InventoryViewModel(get(), get()) }
    factory { CategoryViewModel(get()) }
}