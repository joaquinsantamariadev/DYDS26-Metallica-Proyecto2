package com.app.domain.usecase.settings

import com.app.domain.entity.settings.StoreSettings
import com.app.domain.repository.SettingsRepository

class GetStoreSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(): StoreSettings = settingsRepository.getStoreSettings()
}
