package com.app.presentation.reports.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.ReportFilters

@Composable
fun DateRangePicker(
    filters: ReportFilters,
    onFiltersChanged: (ReportFilters) -> Unit
) {
    // Implementación básica (placeholder funcional)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Desde: ${filters.from}")
        Spacer(modifier = Modifier.width(16.dp))
        Text("Hasta: ${filters.to}")
    }
}
