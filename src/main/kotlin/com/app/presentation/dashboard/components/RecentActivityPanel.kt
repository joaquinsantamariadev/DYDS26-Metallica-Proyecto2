package com.app.presentation.dashboard.components

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
import com.app.domain.entity.dashboard.RecentSaleEntry
import java.time.format.DateTimeFormatter

@Composable
fun RecentActivityPanel(sales: List<RecentSaleEntry>, modifier: Modifier = Modifier) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Actividad reciente", style = MaterialTheme.typography.subtitle1)
            if (sales.isEmpty()) {
                Text(
                    text = "Sin ventas recientes",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                sales.forEach { sale ->
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sale.dateTime.format(formatter),
                            style = MaterialTheme.typography.body2
                        )
                        Text(
                            text = "${sale.itemCount} ítem(s) — ${"%.2f".format(sale.total)}",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}