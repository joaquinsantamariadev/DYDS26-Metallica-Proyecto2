package com.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.presentation.dashboard.components.ExchangeRateWidget
import com.app.presentation.dashboard.components.ExpiryAlertsPanel
import com.app.presentation.dashboard.components.KpiCard
import com.app.presentation.dashboard.components.RecentActivityPanel
import com.app.presentation.dashboard.components.StockAlertsPanel
import org.koin.java.KoinJavaComponent.get

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = get(DashboardViewModel::class.java)
) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        state.error != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = state.error!!, color = MaterialTheme.colors.error)
        }
        else -> Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val metrics = state.metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    title = "Productos",
                    value = metrics?.totalProducts?.toString() ?: "-",
                    icon = Icons.Default.Info
                )
                KpiCard(
                    title = "Valor inventario",
                    value = metrics?.let { "%.2f".format(it.inventoryValue) } ?: "-",
                    icon = Icons.Default.ShoppingCart
                )
                KpiCard(
                    title = "Ventas hoy",
                    value = metrics?.salesToday?.toString() ?: "-",
                    icon = Icons.Default.ShoppingCart
                )
                KpiCard(
                    title = "Sesión de caja",
                    value = if (metrics?.hasActiveSession == true) "Abierta" else "Cerrada",
                    icon = if (metrics?.hasActiveSession == true) Icons.Default.CheckCircle else Icons.Default.Warning
                )
            }
            ExchangeRateWidget(
                exchangeRate = state.exchangeRate,
                unavailable = state.exchangeRateUnavailable,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockAlertsPanel(
                    alerts = state.stockAlerts,
                    modifier = Modifier.weight(1f)
                )
                ExpiryAlertsPanel(
                    alerts = state.expiryAlerts,
                    modifier = Modifier.weight(1f)
                )
            }
            RecentActivityPanel(
                sales = state.recentSales,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}