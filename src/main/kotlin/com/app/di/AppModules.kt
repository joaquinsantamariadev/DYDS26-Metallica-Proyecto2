package com.app.di

import com.app.data.external.OpenFoodFactsClient
import com.app.data.local.sales.CashRegisterLocalDataSource
import com.app.data.local.sales.CashRegisterLocalDataSourceImpl
import com.app.data.local.sales.SaleLocalDataSource
import com.app.data.local.sales.SaleLocalDataSourceImpl
import com.app.data.mapper.CashRegisterMapper
import com.app.data.mapper.SaleItemMapper
import com.app.data.mapper.SaleMapper
import com.app.data.repository.*
import com.app.domain.repository.*
import com.app.domain.usecase.ScanProductUseCase
import com.app.domain.usecase.cart.CalculateCartTotalUseCase
import com.app.domain.usecase.cart.ValidateCartStockUseCase
import com.app.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.domain.usecase.cashregister.OpenCashRegisterUseCase
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

    single<SaleLocalDataSource> { SaleLocalDataSourceImpl() }
    single<CashRegisterLocalDataSource> { CashRegisterLocalDataSourceImpl() }

    single { SaleItemMapper() }
    single { SaleMapper(get()) }
    single { CashRegisterMapper() }

    single<SaleRepository> { SaleRepositoryImpl(get(), get()) }
    single<CashRegisterRepository> { CashRegisterRepositoryImpl(get(), get()) }

    factory { ScanProductUseCase(get(), get()) }
    factory { ValidateCartStockUseCase(get()) }
    factory { CalculateCartTotalUseCase() }
    factory { CompleteSaleUseCase(get(), get(), get()) }
    factory { GetActiveSessionUseCase(get()) }
    factory { OpenCashRegisterUseCase(get(), get()) }
    factory { CloseCashRegisterUseCase(get(), get()) }

    factory { InventoryViewModel(get(), get()) }
    factory { CategoryViewModel(get()) }
    factory { PosViewModel(get(), get(), get(), get(), get()) }
    factory { CashRegisterViewModel(get(), get(), get(), get()) }
}