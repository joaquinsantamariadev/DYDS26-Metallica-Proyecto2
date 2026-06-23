package com.app.presentation.reports.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.app.domain.entity.report.RevenueSummary
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

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
        val onSurfaceColor = MaterialTheme.colors.onSurface
        val labels = remember(summary.dataPoints) { summary.dataPoints.map { it.periodLabel } }

        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val bottomPadding = 40f
            val chartHeight = size.height - bottomPadding
            val maxRevenue = summary.dataPoints.maxOfOrNull { it.revenue } ?: 1.0
            val count = summary.dataPoints.size.coerceAtLeast(1)
            val barWidth = size.width / (count * 2)

            summary.dataPoints.forEachIndexed { index, point ->
                val barHeight = (point.revenue / maxRevenue) * chartHeight
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(x = index * barWidth * 2, y = chartHeight - barHeight.toFloat()),
                    size = Size(width = barWidth, height = barHeight.toFloat())
                )
            }

            drawIntoCanvas { canvas ->
                val skiaFont = Font(null, 11f)
                val skiaPaint = Paint().apply {
                    color = org.jetbrains.skia.Color.makeARGB(
                        (onSurfaceColor.alpha * 255).toInt(),
                        (onSurfaceColor.red * 255).toInt(),
                        (onSurfaceColor.green * 255).toInt(),
                        (onSurfaceColor.blue * 255).toInt()
                    )
                }
                labels.forEachIndexed { index, label ->
                    val textLine = TextLine.make(label, skiaFont)
                    val x = index * barWidth * 2 + barWidth / 2 - textLine.width / 2
                    canvas.nativeCanvas.drawTextLine(textLine, x, size.height - 5f, skiaPaint)
                }
            }
        }
    }
}
