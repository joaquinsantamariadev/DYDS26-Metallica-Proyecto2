package com.app.domain.usecase.settings

import com.app.domain.entity.settings.SystemSettings
import com.app.domain.repository.SettingsRepository

class SaveSystemSettingsUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(settings: SystemSettings) {
        if (settings.defaultLowStockThreshold <= 0) {
            throw IllegalArgumentException("El umbral de stock debe ser mayor a 0")
        }
        if (settings.expiryAlertDays <= 0) {
            throw IllegalArgumentException("Los días de alerta deben ser mayores a 0")
        }
        settingsRepository.saveSystemSettings(settings)
    }
}
