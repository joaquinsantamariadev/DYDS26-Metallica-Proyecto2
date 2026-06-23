package com.app.presentation.settings

import com.app.domain.entity.settings.StoreSettings
import com.app.domain.entity.settings.SystemSettings

data class SettingsUiState(
    val storeSettings: StoreSettings = StoreSettings(),
    val systemSettings: SystemSettings = SystemSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val exportStatus: String? = null,
    val errorMessage: String? = null
)
