package com.app.settings.domain.repository

import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings

interface SettingsRepository {
    suspend fun getStoreSettings(): StoreSettings
    suspend fun saveStoreSettings(settings: StoreSettings)
    suspend fun getSystemSettings(): SystemSettings
    suspend fun saveSystemSettings(settings: SystemSettings)
}
