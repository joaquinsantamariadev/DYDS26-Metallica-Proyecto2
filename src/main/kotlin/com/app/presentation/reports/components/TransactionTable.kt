package com.app.presentation.reports.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.TransactionHistoryEntry

@Composable
fun TransactionTable(
    transactions: List<TransactionHistoryEntry>,
    hasNextPage: Boolean,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) { entry ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = 2.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Venta #${entry.saleId} - ${entry.dateTime}")
                    Text("Total: \$${entry.total} - ${entry.items.size} ítems")
                }
            }
        }
        if (hasNextPage) {
            item {
                Button(onClick = onLoadMore, enabled = !isLoadingMore, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Text(if (isLoadingMore) "Cargando..." else "Cargar más")
                }
            }
        }
    }
}
