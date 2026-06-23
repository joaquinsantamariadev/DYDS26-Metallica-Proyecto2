package com.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.settings.SystemSettings

@Composable
fun SystemSettingsForm(
    settings: SystemSettings,
    onSave: (SystemSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var threshold by remember(settings) { mutableStateOf(settings.defaultLowStockThreshold.toString()) }
    var expiryDays by remember(settings) { mutableStateOf(settings.expiryAlertDays.toString()) }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Parámetros del Sistema", style = MaterialTheme.typography.h6)
        OutlinedTextField(
            value = threshold,
            onValueChange = { if (it.all { char -> char.isDigit() }) threshold = it },
            label = { Text("Umbral de Stock Bajo") }
        )
        OutlinedTextField(
            value = expiryDays,
            onValueChange = { if (it.all { char -> char.isDigit() }) expiryDays = it },
            label = { Text("Días de Alerta de Vencimiento") }
        )
        Button(onClick = {
            onSave(settings.copy(
                defaultLowStockThreshold = threshold.toIntOrNull() ?: settings.defaultLowStockThreshold,
                expiryAlertDays = expiryDays.toIntOrNull() ?: settings.expiryAlertDays
            ))
        }) {
            Text("Guardar Parámetros del Sistema")
        }
    }
}
