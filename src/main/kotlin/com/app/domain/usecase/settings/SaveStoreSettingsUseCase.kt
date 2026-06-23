package com.app.domain.usecase.settings

import com.app.domain.entity.settings.StoreSettings
import com.app.domain.repository.SettingsRepository

class SaveStoreSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(settings: StoreSettings) {
        if (settings.storeName.isBlank()) {
            throw IllegalArgumentException("El nombre del local no puede estar vacío")
        }
        settingsRepository.saveStoreSettings(settings)
    }
}
