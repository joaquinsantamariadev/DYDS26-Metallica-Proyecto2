package com.app.di

import org.koin.dsl.module

val appModule = module {
    // Data Layer
    // single<LocalDataSource> { LocalDataSourceImpl() }
    // single<ExternalDataSource> { ExternalDataSourceImpl() }
    // single<Repository> { RepositoryImpl(get(), get()) }

    // Domain Layer
    // single<UseCase> { UseCaseImpl(get()) }

    // Presentation Layer
    // viewModel { FeatureViewModel(get()) }
}
