package com.app.presentation.reports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.RevenueSummary

@Composable
fun RevenueChart(summary: RevenueSummary) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total: \$${summary.totalRevenue}")
            Text("Ventas: ${summary.totalSales}")
            Text("Ticket Promedio: \$${summary.averageTicket}")
        }
        Spacer(modifier = Modifier.height(32.dp))
        
        val primaryColor = MaterialTheme.colors.primary
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val maxRevenue = summary.dataPoints.maxOfOrNull { it.revenue } ?: 1.0
            val barWidth = size.width / (summary.dataPoints.size * 2).coerceAtLeast(1)
            
            summary.dataPoints.forEachIndexed { index, point ->
                val barHeight = (point.revenue / maxRevenue) * size.height
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(x = index * barWidth * 2, y = size.height - barHeight.toFloat()),
                    size = Size(width = barWidth, height = barHeight.toFloat())
                )
            }
        }
    }
}
