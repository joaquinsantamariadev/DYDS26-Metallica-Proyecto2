package com.app.settings.presentation

import com.app.settings.domain.entity.StoreSettings
import com.app.settings.domain.entity.SystemSettings

data class SettingsUiState(
    val storeSettings: StoreSettings = StoreSettings(),
    val systemSettings: SystemSettings = SystemSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val exportStatus: String? = null,
    val errorMessage: String? = null
)
