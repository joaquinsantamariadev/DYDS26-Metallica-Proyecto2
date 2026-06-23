package com.app.presentation.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.ProductRotationEntry

@Composable
fun RotationPanel(rotation: List<ProductRotationEntry>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(rotation) { index, item ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${index + 1}. ${item.productName} (${item.categoryName})")
                    Column {
                        Text("Unidades: ${item.unitsSold}")
                        Text("Ingresos: \$${item.revenue}")
                    }
                }
            }
        }
    }
}
