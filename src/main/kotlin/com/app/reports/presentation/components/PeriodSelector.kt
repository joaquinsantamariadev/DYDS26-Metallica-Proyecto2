package com.app.reports.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.app.reports.domain.entity.ReportPeriod

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
