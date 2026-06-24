package com.app.common.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.dashboard.presentation.DashboardScreen
import com.app.inventory.presentation.InventoryScreen
import com.app.inventory.presentation.InventoryViewModel
import com.app.pos.presentation.PosScreen
import com.app.pos.presentation.PosViewModel
import com.app.reports.presentation.ReportsScreen
import com.app.settings.presentation.SettingsScreen
import com.app.settings.presentation.SettingsViewModel
import com.app.pos.presentation.cashregister.CashRegisterViewModel
import com.app.dashboard.presentation.DashboardViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
fun NavigationHost(currentScreen: Screen, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        when (currentScreen) {
            is Screen.Dashboard -> {
                val viewModel = get<DashboardViewModel>(DashboardViewModel::class.java)
                DashboardScreen(viewModel)
            }
            is Screen.Inventory -> {
                val viewModel = get<InventoryViewModel>(InventoryViewModel::class.java)
                InventoryScreen(viewModel)
            }
            is Screen.PointOfSale -> {
                val viewModel = get<PosViewModel>(PosViewModel::class.java)
                val cashRegisterViewModel = get<CashRegisterViewModel>(CashRegisterViewModel::class.java)
                PosScreen(viewModel, cashRegisterViewModel)
            }
            is Screen.Reports -> ReportsScreen()
            is Screen.Settings -> {
                val viewModel = get<SettingsViewModel>(SettingsViewModel::class.java)
                SettingsScreen(viewModel)
            }
        }
    }
}
