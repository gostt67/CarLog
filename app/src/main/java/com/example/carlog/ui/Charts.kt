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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.carlog.ui.stats.MonthCost

/**
 * Столбчатая диаграмма расходов по месяцам:
 * топливо (низ столбца) + прочие расходы (верх столбца), с горизонтальной сеткой.
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
    val otherColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val maxValue = data.maxOf { it.fuel + it.other }.takeIf { it > 0.0 } ?: 1.0

    Column(modifier = modifier) {
        Text(
            "Максимум за месяц: ${fmtMoney(maxValue)}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val maxH = size.height * 0.95f
            val gridStroke = 1.dp.toPx()

            // Горизонтальная сетка: 0, 1/3, 2/3, максимум
            for (i in 0..3) {
                val y = size.height - maxH * i / 3f
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = gridStroke
                )
            }

            val slot = size.width / data.size
            val barWidth = slot * 0.5f
            val radius = CornerRadius(6.dp.toPx())

            data.forEachIndexed { i, m ->
                val x = slot * i + (slot - barWidth) / 2f
                val fuelH = (m.fuel / maxValue * maxH).toFloat()
                val otherH = (m.other / maxValue * maxH).toFloat()
                val totalH = fuelH + otherH

                if (otherH > 0f) {
                    drawRoundRect(
                        color = otherColor,
                        topLeft = Offset(x, size.height - totalH),
                        size = Size(barWidth, totalH),
                        cornerRadius = radius
                    )
                }
                if (fuelH > 0f) {
                    drawRoundRect(
                        color = fuelColor,
                        topLeft = Offset(x, size.height - fuelH),
                        size = Size(barWidth, fuelH),
                        cornerRadius = radius
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        // Подписи месяцев под столбцами (текущий месяц выделен)
        Row(Modifier.fillMaxWidth()) {
            data.forEachIndexed { index, item ->
                val isLast = index == data.lastIndex
                Text(
                    text = item.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                    color = if (isLast) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Легенда
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(fuelColor)
            )
            Spacer(Modifier.width(6.dp))
            Text("Топливо", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.width(20.dp))
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(otherColor)
            )
            Spacer(Modifier.width(6.dp))
            Text("Прочие расходы", style = MaterialTheme.typography.labelMedium)
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
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val ringColor = MaterialTheme.colorScheme.surfaceContainerLow
    val min = points.min()
    val max = points.max()
    val last = points.last()

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val stepX = size.width / (points.size - 1)
            val gridStroke = 1.dp.toPx()

            for (i in 0..2) {
                val y = size.height * 0.95f * (1f - i / 2f) + size.height * 0.025f
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = gridStroke
                )
            }

            fun yOf(v: Double): Float =
                if (max == min) size.height / 2f
                else size.height * 0.95f - ((v - min) / (max - min)).toFloat() * size.height * 0.9f

            val line = Path()
            val fill = Path()
            points.forEachIndexed { i, v ->
                val px = stepX * i
                val py = yOf(v)
                if (i == 0) {
                    line.moveTo(px, py)
                    fill.moveTo(px, size.height)
                    fill.lineTo(px, py)
                } else {
                    line.lineTo(px, py)
                    fill.lineTo(px, py)
                }
            }
            fill.lineTo(size.width, size.height)
            fill.close()

            drawPath(
                path = fill,
                brush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
                )
            )
            drawPath(
                path = line,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Последняя точка — крупная, с кольцом
            val lastCenter = Offset(stepX * (points.size - 1), yOf(last))
            drawCircle(color = ringColor, radius = 7.dp.toPx(), center = lastCenter)
            drawCircle(color = lineColor, radius = 4.5.dp.toPx(), center = lastCenter)
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "мин %.2f".format(min),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "сейчас %.2f".format(last),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "макс %.2f".format(max),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
