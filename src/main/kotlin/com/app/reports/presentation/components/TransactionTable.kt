package com.app.reports.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.reports.domain.entity.TransactionHistoryEntry
import java.time.format.DateTimeFormatter

@Composable
fun TransactionTable(
    transactions: List<TransactionHistoryEntry>,
    hasNextPage: Boolean,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean
) {
    val expandedIds = remember { mutableStateListOf<Long>() }
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions, key = { it.saleId }) { entry ->
            val isExpanded = entry.saleId in expandedIds
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        if (isExpanded) expandedIds.remove(entry.saleId)
                        else expandedIds.add(entry.saleId)
                    },
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Venta #${entry.saleId} - ${entry.dateTime.format(formatter)}",
                            fontWeight = FontWeight.Medium
                        )
                        Text("Total: \$${entry.total}")
                    }
                    Text(
                        "${entry.items.size} ítems",
                        style = MaterialTheme.typography.caption
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            entry.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.productName)
                                    Text("${item.quantity} x \$${item.unitPrice} = \$${item.subtotal}")
                                }
                            }
                        }
                    }
                }
            }
        }
        if (hasNextPage) {
            item {
                Button(
                    onClick = onLoadMore,
                    enabled = !isLoadingMore,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text(if (isLoadingMore) "Cargando..." else "Cargar más")
                }
            }
        }
    }
}
