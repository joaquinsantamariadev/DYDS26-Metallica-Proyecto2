package com.app.settings.domain.usecase

import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.repository.SettingsRepository

interface GetStoreSettingsUseCase {
    suspend operator fun invoke(): StoreSettings
}

class GetStoreSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) : GetStoreSettingsUseCase {
    override suspend operator fun invoke(): StoreSettings = settingsRepository.getStoreSettings()
}
