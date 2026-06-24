package com.app.common.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("Dashboard", Icons.Default.Dashboard)
    data object Inventory : Screen("Inventario", Icons.Default.Inventory2)
    data object PointOfSale : Screen("Punto de Venta", Icons.Default.PointOfSale)
    data object Reports : Screen("Reportes", Icons.Default.Assessment)
    data object Settings : Screen("Configuración", Icons.Default.Settings)

    companion object {
        val entries: List<Screen> get() = listOf(Dashboard, Inventory, PointOfSale, Reports, Settings)
    }
}
