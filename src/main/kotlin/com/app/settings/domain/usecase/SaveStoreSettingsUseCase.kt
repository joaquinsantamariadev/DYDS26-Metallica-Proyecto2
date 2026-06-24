package com.app.settings.domain.usecase

import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.repository.SettingsRepository

interface SaveStoreSettingsUseCase {
    suspend operator fun invoke(settings: StoreSettings)
}

class SaveStoreSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) : SaveStoreSettingsUseCase {
    override suspend operator fun invoke(settings: StoreSettings) {
        if (settings.storeName.isBlank()) {
            throw IllegalArgumentException("El nombre del local no puede estar vacío")
        }
        settingsRepository.saveStoreSettings(settings)
    }
}
