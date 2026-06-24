package com.app.settings.domain.usecase

import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.repository.SettingsRepository

interface GetSystemSettingsUseCase {
    suspend operator fun invoke(): SystemSettings
}

class GetSystemSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) : GetSystemSettingsUseCase {
    override suspend operator fun invoke(): SystemSettings = settingsRepository.getSystemSettings()
}
