package com.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.settings.StoreSettings

@Composable
fun StoreSettingsForm(
    settings: StoreSettings,
    onSave: (StoreSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(settings) { mutableStateOf(settings.storeName) }
    var address by remember(settings) { mutableStateOf(settings.address) }
    var phone by remember(settings) { mutableStateOf(settings.phone) }
    var currency by remember(settings) { mutableStateOf(settings.currency) }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Datos del Local", style = MaterialTheme.typography.h6)
        TextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Local") })
        TextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") })
        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") })
        TextField(value = currency, onValueChange = { currency = it }, label = { Text("Moneda") })
        Button(onClick = { onSave(settings.copy(storeName = name, address = address, phone = phone, currency = currency)) }) {
            Text("Guardar Datos del Local")
        }
    }
}
