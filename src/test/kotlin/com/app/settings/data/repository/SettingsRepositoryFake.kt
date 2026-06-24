package com.app.settings.data.repository

import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings
import com.app.settings.domain.repository.SettingsRepository

class SettingsRepositoryFake : SettingsRepository {
    var storeSettingsResult: StoreSettings = StoreSettings()
    var systemSettingsResult: SystemSettings = SystemSettings()

    var shouldThrowError = false
    var shouldThrowOnGetStore = false
    var shouldThrowOnSaveStore = false
    var shouldThrowOnGetSystem = false
    var shouldThrowOnSaveSystem = false

    var saveStoreCalled = false
    var saveSystemCalled = false
    var capturedStoreSettings: StoreSettings? = null
    var capturedSystemSettings: SystemSettings? = null

    override suspend fun getStoreSettings(): StoreSettings {
        if (shouldThrowError || shouldThrowOnGetStore) throw Exception("get store settings error")
        return storeSettingsResult
    }

    override suspend fun saveStoreSettings(settings: StoreSettings) {
        if (shouldThrowError || shouldThrowOnSaveStore) throw Exception("save store settings error")
        saveStoreCalled = true
        capturedStoreSettings = settings
    }

    override suspend fun getSystemSettings(): SystemSettings {
        if (shouldThrowError || shouldThrowOnGetSystem) throw Exception("get system settings error")
        return systemSettingsResult
    }

    override suspend fun saveSystemSettings(settings: SystemSettings) {
        if (shouldThrowError || shouldThrowOnSaveSystem) throw Exception("save system settings error")
        saveSystemCalled = true
        capturedSystemSettings = settings
    }
}
