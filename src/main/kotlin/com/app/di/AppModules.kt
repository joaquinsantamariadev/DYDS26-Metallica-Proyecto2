package com.app.di

import com.app.data.external.OpenFoodFactsClient
import com.app.data.repository.*
import com.app.domain.repository.*
import com.app.domain.usecase.ScanProductUseCase
import com.app.domain.usecase.cart.ValidateCartStockUseCase
import com.app.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.cashregister.OpenCashRegisterUseCase
import com.app.domain.usecase.dashboard.GetDashboardMetricsUseCase
import com.app.domain.usecase.dashboard.GetExpiryAlertsUseCase
import com.app.domain.usecase.dashboard.GetLowStockAlertsUseCase
import com.app.domain.usecase.dashboard.GetRecentSalesUseCase
import com.app.domain.usecase.sale.CompleteSaleUseCase
import com.app.presentation.inventory.CategoryViewModel
import com.app.presentation.inventory.InventoryViewModel
import com.app.presentation.pos.PosViewModel
import com.app.presentation.pos.cashregister.CashRegisterViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
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

    single<SaleRepository> { SaleRepositoryImpl() }
    single<CashRegisterRepository> { CashRegisterRepositoryImpl() }

    factory { ScanProductUseCase(get(), get()) }
    factory { ValidateCartStockUseCase(get()) }
    factory { CompleteSaleUseCase(get(), get(), get()) }
    factory { GetActiveSessionUseCase(get()) }
    factory { OpenCashRegisterUseCase(get(), get()) }
    factory { CloseCashRegisterUseCase(get(), get()) }

    factory { InventoryViewModel(get(), get()) }
    factory { CategoryViewModel(get()) }
    factory { PosViewModel(get(), get(), get(), get()) }
    factory { CashRegisterViewModel(get(), get(), get(), get()) }

    single<DashboardRepository> { DashboardRepositoryImpl() }

    factory { GetDashboardMetricsUseCase(get()) }
    factory { GetLowStockAlertsUseCase(get()) }
    factory { GetExpiryAlertsUseCase(get()) }
    factory { GetRecentSalesUseCase(get()) }

}