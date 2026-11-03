package com.example.carlog.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.carlog.ui.stats.MonthCost

/**
 * Столбчатая диаграмма расходов по месяцам:
 * топливо (низ столбца) + прочие расходы (верх столбца).
 */
@Composable
fun MonthlyStackedBarChart(
    data: List<MonthCost>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty() || data.all { it.fuel <= 0.0 && it.other <= 0.0 }) {
        Text(
            "Пока нет данных за последние месяцы",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val fuelColor = MaterialTheme.colorScheme.primary
    val otherColor = MaterialTheme.colorScheme.tertiary

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val maxValue = data.maxOf { it.fuel + it.other }.takeIf { it > 0.0 } ?: 1.0
            val slot = size.width / data.size
            val barWidth = slot * 0.55f

            data.forEachIndexed { i, m ->
                val x = slot * i + (slot - barWidth) / 2f
                val fuelH = (m.fuel / maxValue * size.height * 0.95).toFloat()
                val otherH = (m.other / maxValue * size.height * 0.95).toFloat()

                if (fuelH > 0f) {
                    drawRect(
                        color = fuelColor,
                        topLeft = Offset(x, size.height - fuelH),
                        size = Size(barWidth, fuelH)
                    )
                }
                if (otherH > 0f) {
                    drawRect(
                        color = otherColor,
                        topLeft = Offset(x, size.height - fuelH - otherH),
                        size = Size(barWidth, otherH)
                    )
                }
            }
        }

        // Подписи месяцев под столбцами
        Row(Modifier.fillMaxWidth()) {
            data.forEach {
                Text(
                    text = it.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Легенда
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(fuelColor))
            Spacer(Modifier.width(4.dp))
            Text("Топливо", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.width(16.dp))
            Box(Modifier.size(10.dp).background(otherColor))
            Spacer(Modifier.width(4.dp))
            Text("Прочие расходы", style = MaterialTheme.typography.labelSmall)
        }
    }
}

/** Линейный график динамики цены за литр (по заправкам, от старых к новым). */
@Composable
fun PriceLineChart(
    points: List<Double>,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) {
        Text(
            "Нужно минимум две заправки для графика",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val lineColor = MaterialTheme.colorScheme.primary
    val min = points.min()
    val max = points.max()

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val stepX = size.width / (points.size - 1)

            fun yOf(v: Double): Float =
                if (max == min) size.height / 2f
                else size.height * 0.95f - ((v - min) / (max - min)).toFloat() * size.height * 0.9f

            val path = Path()
            points.forEachIndexed { i, v ->
                val px = stepX * i
                val py = yOf(v)
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            drawPath(path, color = lineColor, style = Stroke(width = 5f))

            points.forEachIndexed { i, v ->
                drawCircle(
                    color = lineColor,
                    radius = 6f,
                    center = Offset(stepX * i, yOf(v))
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "мин %.2f".format(min),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "макс %.2f".format(max),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
