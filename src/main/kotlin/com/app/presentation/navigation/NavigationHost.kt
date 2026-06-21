package com.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.presentation.dashboard.DashboardScreen
import com.app.presentation.inventory.InventoryScreen
import com.app.presentation.inventory.InventoryViewModel
import com.app.presentation.pos.PosScreen
import com.app.presentation.pos.PosViewModel
import com.app.presentation.reports.ReportsScreen
import com.app.presentation.settings.SettingsScreen
import org.koin.java.KoinJavaComponent.get
@Composable
fun NavigationHost(currentScreen: Screen, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            is Screen.Dashboard -> DashboardScreen()
            is Screen.Inventory -> {
                val viewModel = get<InventoryViewModel>(InventoryViewModel::class.java)
                InventoryScreen(viewModel)
            }
            is Screen.PointOfSale -> {
                val viewModel = get<PosViewModel>(PosViewModel::class.java)
                PosScreen(viewModel)
            }
            is Screen.Reports -> ReportsScreen()
            is Screen.Settings -> SettingsScreen()
        }
    }
}
