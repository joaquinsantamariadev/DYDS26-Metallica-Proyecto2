package com.app.reports.presentation.components

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
import androidx.compose.material.OutlinedButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.reports.domain.entity.ReportFilters
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun DateRangePicker(
    filters: ReportFilters,
    onFiltersChanged: (ReportFilters) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            onClick = { showFromPicker = true },
            modifier = Modifier.width(160.dp)
        ) {
            Text("Desde: ${filters.from.format(formatter)}")
        }

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedButton(
            onClick = { showToPicker = true },
            modifier = Modifier.width(160.dp)
        ) {
            Text("Hasta: ${filters.to.format(formatter)}")
        }
    }

    if (showFromPicker) {
        CustomDatePickerDialog(
            initialDate = filters.from,
            onDateSelected = { selectedDate ->
                onFiltersChanged(filters.copy(from = selectedDate))
            },
            onDismissRequest = { showFromPicker = false }
        )
    }

    if (showToPicker) {
        CustomDatePickerDialog(
            initialDate = filters.to,
            onDateSelected = { selectedDate ->
                onFiltersChanged(filters.copy(to = selectedDate))
            },
            onDismissRequest = { showToPicker = false }
        )
    }
}
