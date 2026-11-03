package com.example.carlog.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlog.data.CarLogRepository
import com.example.carlog.data.FuelEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

/** Расходы за один месяц (для столбчатой диаграммы). */
data class MonthCost(
    val label: String,
    val fuel: Double,
    val other: Double
)

data class StatsState(
    val avgConsumption: Double? = null, // средний расход, л/100км
    val costPerKm: Double? = null,      // стоимость 1 км пути (по топливу)
    val totalDistance: Int = 0,         // учтённый пробег, км
    val totalFuelCost: Double = 0.0,
    val totalOtherCost: Double = 0.0,
    val monthly: List<MonthCost> = emptyList(),
    val priceHistory: List<Double> = emptyList(),
    val hasData: Boolean = false
)

fun yearMonthOf(millis: Long): YearMonth =
    YearMonth.from(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

fun monthLabel(ym: YearMonth): String = when (ym.monthValue) {
    1 -> "Янв"; 2 -> "Фев"; 3 -> "Мар"; 4 -> "Апр"
    5 -> "Май"; 6 -> "Июн"; 7 -> "Июл"; 8 -> "Авг"
    9 -> "Сен"; 10 -> "Окт"; 11 -> "Ноя"; else -> "Дек"
}

/**
 * Средний расход по методу полных баков:
 * литры, залитые между двумя полными заправками (включая вторую),
 * делённые на пройденное между ними расстояние.
 *
 * [fuelAsc] — заправки, отсортированные по возрастанию пробега.
 */
fun computeAvgConsumption(fuelAsc: List<FuelEntry>): Double? {
    val full = fuelAsc.filter { it.fullTank }
    if (full.size < 2) return null

    var liters = 0.0
    var distance = 0

    for (i in 1 until full.size) {
        val from = full[i - 1]
        val to = full[i]
        val segDist = to.odometer - from.odometer
        if (segDist <= 0) continue
        liters += fuelAsc
            .filter { it.odometer > from.odometer && it.odometer <= to.odometer }
            .sumOf { it.liters }
        distance += segDist
    }

    return if (distance > 0) liters / distance * 100.0 else null
}

class StatsViewModel(repo: CarLogRepository) : ViewModel() {

    val state: StateFlow<StatsState> =
        combine(repo.fuelByOdometer, repo.expenses) { fuel, expenses ->
            val months = (0L..5L).map { YearMonth.now().minusMonths(5L - it) }

            val monthly = months.map { ym ->
                MonthCost(
                    label = monthLabel(ym),
                    fuel = fuel.filter { yearMonthOf(it.date) == ym }.sumOf { it.totalCost },
                    other = expenses.filter { yearMonthOf(it.date) == ym }.sumOf { it.amount }
                )
            }

            val distance =
                if (fuel.size >= 2) fuel.last().odometer - fuel.first().odometer else 0
            val fuelCost = fuel.sumOf { it.totalCost }

            StatsState(
                avgConsumption = computeAvgConsumption(fuel),
                costPerKm = if (distance > 0) fuelCost / distance else null,
                totalDistance = distance,
                totalFuelCost = fuelCost,
                totalOtherCost = expenses.sumOf { it.amount },
                monthly = monthly,
                priceHistory = fuel.map { it.pricePerLiter },
                hasData = fuel.isNotEmpty() || expenses.isNotEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsState()
        )
}
