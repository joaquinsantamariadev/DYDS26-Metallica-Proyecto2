package com.app.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.presentation.navigation.NavigationHost
import com.app.presentation.navigation.Screen
import com.app.presentation.utils.AppTheme

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavigationHost(currentScreen = currentScreen)
        }
    }
}
