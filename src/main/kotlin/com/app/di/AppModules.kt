package com.app.di

import com.app.data.repository.ExchangeRateRepositoryImpl
import com.app.data.repository.InventoryRepositoryImpl
import com.app.domain.repository.ExchangeRateRepository
import com.app.domain.repository.InventoryRepository
import org.koin.dsl.module

val appModule = module {
    single<InventoryRepository> { InventoryRepositoryImpl() }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl() }
}
