package com.app.domain.usecase.settings

import com.app.domain.entity.settings.SystemSettings
import com.app.domain.repository.SettingsRepository

class GetSystemSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(): SystemSettings = settingsRepository.getSystemSettings()
}
