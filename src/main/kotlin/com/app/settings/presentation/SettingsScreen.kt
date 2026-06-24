package com.app.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.settings.presentation.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            StoreSettingsForm(
                settings = uiState.storeSettings,
                onSave = { viewModel.saveStoreSettings(it) }
            )
            Divider()
            SystemSettingsForm(
                settings = uiState.systemSettings,
                onSave = { viewModel.saveSystemSettings(it) }
            )
            Divider()
            ExportPanel(
                onExport = { path, format -> viewModel.exportData(path, format) }
            )
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colors.error)
            }
        }
    }
}
