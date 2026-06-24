package com.app.di

import com.app.inventory.data.openfoodfacts.OpenFoodFactsClient
import com.app.exchangerate.domain.repository.ExchangeRateRemoteSource
import com.app.exchangerate.data.dolarapi.ExchangeRateRemoteDataSource
import com.app.inventory.data.repository.InventoryRepositoryImpl
import com.app.exchangerate.data.repository.ExchangeRateRepositoryImpl
import com.app.inventory.data.repository.CategoryRepositoryImpl
import com.app.pos.data.repository.SaleRepositoryImpl
import com.app.pos.data.repository.CashRegisterRepositoryImpl
import com.app.settings.data.repository.SettingsRepositoryImpl
import com.app.settings.data.repository.ExportRepositoryImpl
import com.app.dashboard.data.repository.DashboardRepositoryImpl
import com.app.reports.data.repository.ReportsRepositoryImpl
import com.app.inventory.domain.repository.InventoryRepository
import com.app.exchangerate.domain.repository.ExchangeRateRepository
import com.app.inventory.domain.repository.CategoryRepository
import com.app.inventory.domain.repository.ProductExternalSource
import com.app.pos.domain.repository.SaleRepository
import com.app.pos.domain.repository.CashRegisterRepository
import com.app.settings.domain.repository.SettingsRepository
import com.app.settings.domain.repository.ExportRepository
import com.app.dashboard.domain.repository.DashboardRepository
import com.app.reports.domain.repository.ReportsRepository
import com.app.inventory.domain.usecase.ScanProductUseCase
import com.app.inventory.domain.usecase.ScanProductUseCaseImpl
import com.app.pos.domain.usecase.cart.ValidateCartStockUseCase
import com.app.pos.domain.usecase.cart.ValidateCartStockUseCaseImpl
import com.app.pos.domain.usecase.cashregister.CloseCashRegisterUseCase
import com.app.pos.domain.usecase.cashregister.CloseCashRegisterUseCaseImpl
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCase
import com.app.pos.domain.usecase.cashregister.GetActiveSessionUseCaseImpl
import com.app.pos.domain.usecase.cashregister.OpenCashRegisterUseCase
import com.app.pos.domain.usecase.cashregister.OpenCashRegisterUseCaseImpl
import com.app.dashboard.domain.usecase.GetDashboardMetricsUseCase
import com.app.dashboard.domain.usecase.GetDashboardMetricsUseCaseImpl
import com.app.dashboard.domain.usecase.GetExpiryAlertsUseCase
import com.app.dashboard.domain.usecase.GetExpiryAlertsUseCaseImpl
import com.app.dashboard.domain.usecase.GetLowStockAlertsUseCase
import com.app.dashboard.domain.usecase.GetLowStockAlertsUseCaseImpl
import com.app.dashboard.domain.usecase.GetRecentSalesUseCase
import com.app.dashboard.domain.usecase.GetRecentSalesUseCaseImpl
import com.app.exchangerate.domain.usecase.GetExchangeRateUseCase
import com.app.exchangerate.domain.usecase.GetExchangeRateUseCaseImpl
import com.app.pos.domain.usecase.sale.CompleteSaleUseCase
import com.app.pos.domain.usecase.sale.CompleteSaleUseCaseImpl
import com.app.settings.domain.usecase.GetStoreSettingsUseCase
import com.app.settings.domain.usecase.GetStoreSettingsUseCaseImpl
import com.app.settings.domain.usecase.GetSystemSettingsUseCase
import com.app.settings.domain.usecase.GetSystemSettingsUseCaseImpl
import com.app.settings.domain.usecase.SaveStoreSettingsUseCase
import com.app.settings.domain.usecase.SaveStoreSettingsUseCaseImpl
import com.app.settings.domain.usecase.SaveSystemSettingsUseCase
import com.app.settings.domain.usecase.SaveSystemSettingsUseCaseImpl
import com.app.settings.domain.usecase.ExportDataUseCase
import com.app.settings.domain.usecase.ExportDataUseCaseImpl
import com.app.reports.domain.usecase.GetTransactionHistoryUseCase
import com.app.reports.domain.usecase.GetTransactionHistoryUseCaseImpl
import com.app.reports.domain.usecase.GetRevenueSummaryUseCase
import com.app.reports.domain.usecase.GetRevenueSummaryUseCaseImpl
import com.app.reports.domain.usecase.GetProductRotationUseCase
import com.app.reports.domain.usecase.GetProductRotationUseCaseImpl
import com.app.reports.domain.usecase.GetMarginsUseCase
import com.app.reports.domain.usecase.GetMarginsUseCaseImpl
import com.app.dashboard.presentation.DashboardViewModel
import com.app.inventory.presentation.CategoryViewModel
import com.app.inventory.presentation.InventoryViewModel
import com.app.pos.presentation.PosViewModel
import com.app.pos.presentation.cashregister.CashRegisterViewModel
import com.app.settings.presentation.SettingsViewModel
import com.app.reports.presentation.StatisticsViewModel
import com.app.reports.presentation.TransactionHistoryViewModel

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

    single<InventoryRepository> { InventoryRepositoryImpl(get()) }
    single<ExchangeRateRemoteSource> { ExchangeRateRemoteDataSource(get()) }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<ProductExternalSource> { OpenFoodFactsClient(get()) }

    single<SaleRepository> { SaleRepositoryImpl() }
    single<CashRegisterRepository> { CashRegisterRepositoryImpl() }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<ExportRepository> { ExportRepositoryImpl() }

    factory<ScanProductUseCase> { ScanProductUseCaseImpl(get()) }
    factory<ValidateCartStockUseCase> { ValidateCartStockUseCaseImpl(get()) }
    factory<CompleteSaleUseCase> { CompleteSaleUseCaseImpl(get(), get(), get()) }
    factory<GetActiveSessionUseCase> { GetActiveSessionUseCaseImpl(get()) }
    factory<OpenCashRegisterUseCase> { OpenCashRegisterUseCaseImpl(get(), get()) }
    factory<CloseCashRegisterUseCase> { CloseCashRegisterUseCaseImpl(get(), get()) }

    factory<GetStoreSettingsUseCase> { GetStoreSettingsUseCaseImpl(get()) }
    factory<GetSystemSettingsUseCase> { GetSystemSettingsUseCaseImpl(get()) }
    factory<SaveStoreSettingsUseCase> { SaveStoreSettingsUseCaseImpl(get()) }
    factory<SaveSystemSettingsUseCase> { SaveSystemSettingsUseCaseImpl(get()) }
    factory<ExportDataUseCase> { ExportDataUseCaseImpl(get()) }
    factory<GetExchangeRateUseCase> { GetExchangeRateUseCaseImpl(get()) }

    factory { InventoryViewModel(get(), get(), get()) }
    factory { CategoryViewModel(get()) }
    factory { PosViewModel(get(), get(), get(), get()) }
    factory { CashRegisterViewModel(get(), get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get(), get(), get()) }

    single<DashboardRepository> { DashboardRepositoryImpl() }

    factory<GetDashboardMetricsUseCase> { GetDashboardMetricsUseCaseImpl(get()) }
    factory<GetLowStockAlertsUseCase> { GetLowStockAlertsUseCaseImpl(get()) }
    factory<GetExpiryAlertsUseCase> { GetExpiryAlertsUseCaseImpl(get()) }
    factory<GetRecentSalesUseCase> { GetRecentSalesUseCaseImpl(get()) }
    factory { DashboardViewModel(get(), get(), get(), get(), get()) }

    single<ReportsRepository> { ReportsRepositoryImpl() }

    factory<GetTransactionHistoryUseCase> { GetTransactionHistoryUseCaseImpl(get()) }
    factory<GetRevenueSummaryUseCase> { GetRevenueSummaryUseCaseImpl(get()) }
    factory<GetProductRotationUseCase> { GetProductRotationUseCaseImpl(get()) }
    factory<GetMarginsUseCase> { GetMarginsUseCaseImpl(get()) }

    factory { TransactionHistoryViewModel(get()) }
    factory { StatisticsViewModel(get(), get(), get()) }
}