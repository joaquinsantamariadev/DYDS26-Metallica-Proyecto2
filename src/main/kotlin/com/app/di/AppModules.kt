package com.app.di

import com.app.data.external.OpenFoodFactsClient
import com.app.data.repository.ExchangeRateRepositoryImpl
import com.app.data.repository.InventoryRepositoryImpl
import com.app.domain.repository.ExchangeRateRepository
import com.app.domain.repository.InventoryRepository
import com.app.domain.usecase.ScanProductUseCase
import com.app.presentation.inventory.InventoryViewModel
import org.koin.dsl.module

val appModule = module {
    single<InventoryRepository> { InventoryRepositoryImpl() }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl() }
    single { OpenFoodFactsClient() }
    factory { ScanProductUseCase(get(), get()) }
    factory { InventoryViewModel(get(), get()) }
}
