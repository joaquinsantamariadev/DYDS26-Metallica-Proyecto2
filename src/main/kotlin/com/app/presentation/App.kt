package com.app.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.common.presentation.navigation.NavigationHost
import com.app.common.presentation.navigation.Screen
import com.app.common.presentation.utils.AppTheme
import com.app.common.presentation.utils.Sidebar
import com.app.settings.presentation.SettingsViewModel
import org.koin.java.KoinJavaComponent.get
import androidx.compose.runtime.collectAsState

@Composable
fun App(settingsViewModel: SettingsViewModel = get(SettingsViewModel::class.java)) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    val settingsState by settingsViewModel.uiState.collectAsState()
    val isDarkMode = settingsState.systemSettings?.isDarkMode ?: false

    AppTheme(darkTheme = isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                Sidebar(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
                NavigationHost(
                    currentScreen = currentScreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
