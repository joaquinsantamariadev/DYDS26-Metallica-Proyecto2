package com.app.presentation.reports.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.ReportFilters
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun DateRangePicker(
    filters: ReportFilters,
    onFiltersChanged: (ReportFilters) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    var fromText by remember(filters.from) { mutableStateOf(filters.from.format(formatter)) }
    var toText by remember(filters.to) { mutableStateOf(filters.to.format(formatter)) }
    var fromError by remember { mutableStateOf(false) }
    var toError by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = fromText,
            onValueChange = { value ->
                fromText = value
                try {
                    val date = LocalDate.parse(value, formatter)
                    fromError = false
                    onFiltersChanged(filters.copy(from = date))
                } catch (_: DateTimeParseException) {
                    fromError = true
                }
            },
            label = { Text("Desde") },
            isError = fromError,
            singleLine = true,
            modifier = Modifier.width(160.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            value = toText,
            onValueChange = { value ->
                toText = value
                try {
                    val date = LocalDate.parse(value, formatter)
                    toError = false
                    onFiltersChanged(filters.copy(to = date))
                } catch (_: DateTimeParseException) {
                    toError = true
                }
            },
            label = { Text("Hasta") },
            isError = toError,
            singleLine = true,
            modifier = Modifier.width(160.dp)
        )
    }
}
