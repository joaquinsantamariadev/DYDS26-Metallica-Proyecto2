package com.app.presentation.reports.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.app.domain.entity.report.ReportPeriod

@Composable
fun PeriodSelector(
    currentPeriod: ReportPeriod,
    onPeriodSelected: (ReportPeriod) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ReportPeriod.entries.forEach { period ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentPeriod == period,
                    onClick = { onPeriodSelected(period) }
                )
                Text(text = period.name)
            }
        }
    }
}
