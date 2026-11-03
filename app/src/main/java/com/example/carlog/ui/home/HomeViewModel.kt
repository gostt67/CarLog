package com.example.carlog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlog.data.CarLogRepository
import com.example.carlog.data.FuelEntry
import com.example.carlog.ui.stats.computeAvgConsumption
import com.example.carlog.ui.stats.yearMonthOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

data class HomeState(
    val avgConsumption: Double? = null,
    val monthFuelCost: Double = 0.0,
    val monthExpenseCost: Double = 0.0,
    val lastFuel: FuelEntry? = null,
    val fuelCount: Int = 0,
    val expenseCount: Int = 0
)

class HomeViewModel(repo: CarLogRepository) : ViewModel() {

    val state: StateFlow<HomeState> =
        combine(repo.fuelByOdometer, repo.expenses) { fuel, expenses ->
            val now = YearMonth.now()
            HomeState(
                avgConsumption = computeAvgConsumption(fuel),
                monthFuelCost = fuel
                    .filter { yearMonthOf(it.date) == now }
                    .sumOf { it.totalCost },
                monthExpenseCost = expenses
                    .filter { yearMonthOf(it.date) == now }
                    .sumOf { it.amount },
                lastFuel = fuel.lastOrNull(),
                fuelCount = fuel.size,
                expenseCount = expenses.size
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeState()
        )
}
