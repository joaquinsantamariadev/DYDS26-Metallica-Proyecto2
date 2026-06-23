package com.app.presentation.settings

import com.app.domain.entity.settings.ExportFormat
import com.app.domain.entity.settings.StoreSettings
import com.app.domain.entity.settings.SystemSettings
import com.app.domain.usecase.settings.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getStoreSettingsUseCase: GetStoreSettingsUseCase,
    private val getSystemSettingsUseCase: GetSystemSettingsUseCase,
    private val saveStoreSettingsUseCase: SaveStoreSettingsUseCase,
    private val saveSystemSettingsUseCase: SaveSystemSettingsUseCase,
    private val exportDataUseCase: ExportDataUseCase,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val storeSettings = getStoreSettingsUseCase()
                val systemSettings = getSystemSettingsUseCase()
                _uiState.update {
                    it.copy(
                        storeSettings = storeSettings,
                        systemSettings = systemSettings,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun saveStoreSettings(settings: StoreSettings) {
        scope.launch {
            _uiState.update { it.copy(isSaving = true, saveSuccess = false, errorMessage = null) }
            try {
                saveStoreSettingsUseCase(settings)
                _uiState.update { it.copy(storeSettings = settings, isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false, errorMessage = e.message) }
            }
        }
    }

    fun saveSystemSettings(settings: SystemSettings) {
        scope.launch {
            _uiState.update { it.copy(isSaving = true, saveSuccess = false, errorMessage = null) }
            try {
                saveSystemSettingsUseCase(settings)
                _uiState.update { it.copy(systemSettings = settings, isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, saveSuccess = false, errorMessage = e.message) }
            }
        }
    }

    fun exportData(path: String, format: ExportFormat) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, exportStatus = null, errorMessage = null) }
            try {
                exportDataUseCase(path, format)
                _uiState.update { it.copy(isLoading = false, exportStatus = "Éxito") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, exportStatus = "Error: ${e.message}") }
            }
        }
    }
}
