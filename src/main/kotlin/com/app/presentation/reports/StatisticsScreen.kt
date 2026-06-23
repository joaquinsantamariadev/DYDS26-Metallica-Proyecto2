package com.app.presentation.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.presentation.reports.components.DateRangePicker
import com.app.presentation.reports.components.MarginPanel
import com.app.presentation.reports.components.PeriodSelector
import com.app.presentation.reports.components.RevenueChart
import com.app.presentation.reports.components.RotationPanel

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel, activeTab: String) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (activeTab != "Márgenes") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateRangePicker(
                    filters = state.filters,
                    onFiltersChanged = { viewModel.onFiltersChanged(it) }
                )
                if (activeTab == "Ingresos") {
                    PeriodSelector(
                        currentPeriod = state.filters.period,
                        onPeriodSelected = { newPeriod ->
                            viewModel.onFiltersChanged(state.filters.copy(period = newPeriod))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (activeTab) {
            "Ingresos" -> {
                if (state.isLoadingRevenue) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.revenueSummary != null) {
                    RevenueChart(summary = state.revenueSummary!!)
                }
            }
            "Rotación" -> {
                if (state.isLoadingRotation) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    RotationPanel(rotation = state.rotation)
                }
            }
            "Márgenes" -> {
                if (state.isLoadingMargins) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    MarginPanel(margins = state.margins)
                }
            }
        }
    }
}
