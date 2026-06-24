package com.app.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.dashboard.domain.entity.ExpiryAlert
import java.time.format.DateTimeFormatter

@Composable
fun ExpiryAlertsPanel(alerts: List<ExpiryAlert>, modifier: Modifier = Modifier) {
    val sorted = alerts.sortedBy { it.daysRemaining }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Vencimientos próximos", style = MaterialTheme.typography.subtitle1)
            if (sorted.isEmpty()) {
                Text(
                    text = "Sin alertas",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                sorted.forEach { alert ->
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = alert.productName, style = MaterialTheme.typography.body2)
                        Text(
                            text = "${alert.expiryDate.format(formatter)} (${alert.daysRemaining}d)",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}