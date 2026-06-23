package com.app.domain.repository

import com.app.domain.entity.settings.StoreSettings
import com.app.domain.entity.settings.SystemSettings

interface SettingsRepository {
    suspend fun getStoreSettings(): StoreSettings
    suspend fun saveStoreSettings(settings: StoreSettings)
    suspend fun getSystemSettings(): SystemSettings
    suspend fun saveSystemSettings(settings: SystemSettings)
}
