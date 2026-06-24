package com.app.settings.domain.usecase

import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.repository.SettingsRepository

interface SaveSystemSettingsUseCase {
    suspend operator fun invoke(settings: SystemSettings)
}

class SaveSystemSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) : SaveSystemSettingsUseCase {
    override suspend operator fun invoke(settings: SystemSettings) {
        if (settings.defaultLowStockThreshold <= 0) {
            throw IllegalArgumentException("El umbral de stock debe ser mayor a 0")
        }
        if (settings.expiryAlertDays <= 0) {
            throw IllegalArgumentException("Los días de alerta deben ser mayores a 0")
        }
        settingsRepository.saveSystemSettings(settings)
    }
}
