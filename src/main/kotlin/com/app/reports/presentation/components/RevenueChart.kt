package com.app.reports.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.reports.domain.entity.RevenueSummary
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

@Composable
fun RevenueChart(summary: RevenueSummary) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total: \$${summary.totalRevenue}", fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary, fontSize = 16.sp)
            Text("Ventas: ${summary.totalSales}", fontWeight = FontWeight.Medium, color = MaterialTheme.colors.onSurface)
            val formattedTicket = "%.2f".format(summary.averageTicket)
            Text("Ticket Promedio: \$$formattedTicket", fontWeight = FontWeight.Medium, color = MaterialTheme.colors.onSurface)
        }
        Spacer(modifier = Modifier.height(32.dp))

        val primaryColor = MaterialTheme.colors.primary
        val primaryVariant = MaterialTheme.colors.primaryVariant
        val onSurfaceColor = MaterialTheme.colors.onSurface
        val gridColor = onSurfaceColor.copy(alpha = 0.1f)
        
        val labels = remember(summary.dataPoints) { summary.dataPoints.map { it.periodLabel } }

        Canvas(modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 16.dp, end = 16.dp)) {
            val bottomPadding = 40f
            val topPadding = 40f
            val chartHeight = size.height - bottomPadding - topPadding
            val maxRevenue = summary.dataPoints.maxOfOrNull { it.revenue }?.takeIf { it > 0 } ?: 1.0
            val count = summary.dataPoints.size.coerceAtLeast(1)
            val barWidth = (size.width / (count * 2f)).coerceAtMost(100f)
            val startOffset = (size.width - (barWidth * 2f * count)) / 2f + barWidth / 2f

            val steps = 4
            for (i in 0..steps) {
                val y = topPadding + chartHeight - (i * chartHeight / steps)
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.5f
                )
            }

            summary.dataPoints.forEachIndexed { index, point ->
                val barHeight = ((point.revenue / maxRevenue) * chartHeight).toFloat()
                val x = startOffset + index * barWidth * 2f
                val y = topPadding + chartHeight - barHeight

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.5f)),
                        startY = y,
                        endY = y + barHeight
                    ),
                    topLeft = Offset(x = x, y = y),
                    size = Size(width = barWidth, height = barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                drawIntoCanvas { canvas ->
                    val skiaPaint = Paint().apply {
                        color = org.jetbrains.skia.Color.makeARGB(
                            (onSurfaceColor.alpha * 255).toInt(),
                            (onSurfaceColor.red * 255).toInt(),
                            (onSurfaceColor.green * 255).toInt(),
                            (onSurfaceColor.blue * 255).toInt()
                        )
                    }
                    
                    val valueFont = Font(null, 12f)
                    val valueText = "\$${"%.0f".format(point.revenue)}"
                    val valueLine = TextLine.make(valueText, valueFont)
                    canvas.nativeCanvas.drawTextLine(valueLine, x + barWidth / 2 - valueLine.width / 2, y - 10f, skiaPaint)

                    val labelFont = Font(null, 13f)
                    val textLine = TextLine.make(labels[index], labelFont)
                    canvas.nativeCanvas.drawTextLine(textLine, x + barWidth / 2 - textLine.width / 2, size.height - 10f, skiaPaint)
                }
            }
        }
    }
}
