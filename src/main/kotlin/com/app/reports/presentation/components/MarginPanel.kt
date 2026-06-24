package com.app.reports.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.reports.domain.entity.MarginEntry

@Composable
fun MarginPanel(margins: List<MarginEntry>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(margins) { margin ->
            val bgColor = when {
                margin.grossMarginPercent > 25 -> Color(0xFF81C784).copy(alpha = 0.2f)
                margin.grossMarginPercent >= 10 -> MaterialTheme.colors.primary.copy(alpha = 0.15f)
                else -> MaterialTheme.colors.error.copy(alpha = 0.15f)
            }
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
                Column(modifier = Modifier.background(bgColor).padding(16.dp)) {
                    Text(text = "${margin.productName} (${margin.categoryName})", modifier = Modifier.padding(bottom = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Costo: \$${margin.costPrice}")
                        Text("Precio: \$${margin.salePrice}")
                        Text("Margen: \$${margin.grossMargin} (${String.format("%.1f", margin.grossMarginPercent)}%)")
                    }
                }
            }
        }
    }
}
