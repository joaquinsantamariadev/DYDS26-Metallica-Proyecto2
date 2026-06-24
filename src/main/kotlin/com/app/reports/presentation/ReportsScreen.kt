package com.app.reports.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.java.KoinJavaComponent.get

@Composable
fun ReportsScreen(
    transactionViewModel: TransactionHistoryViewModel = get(TransactionHistoryViewModel::class.java),
    statisticsViewModel: StatisticsViewModel = get(StatisticsViewModel::class.java)
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Historial", "Ingresos", "Rotación", "Márgenes")
    val icons = listOf(Icons.Default.List, Icons.Default.BarChart, Icons.Default.SwapVert, Icons.Default.AttachMoney)

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex, backgroundColor = MaterialTheme.colors.surface) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    icon = { Icon(icons[index], contentDescription = null) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTabIndex) {
                0 -> TransactionHistoryScreen(viewModel = transactionViewModel)
                1 -> StatisticsScreen(viewModel = statisticsViewModel, activeTab = "Ingresos")
                2 -> StatisticsScreen(viewModel = statisticsViewModel, activeTab = "Rotación")
                3 -> StatisticsScreen(viewModel = statisticsViewModel, activeTab = "Márgenes")
            }
        }
    }
}
