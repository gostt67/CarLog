package com.example.carlog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.carlog.data.CarLogRepository
import com.example.carlog.ui.expenses.ExpenseViewModel
import com.example.carlog.ui.fuel.FuelViewModel
import com.example.carlog.ui.home.HomeViewModel
import com.example.carlog.ui.stats.StatsViewModel

/** Единая фабрика ViewModel — все они получают общий репозиторий. */
class CarLogViewModelFactory(
    private val repo: CarLogRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        HomeViewModel::class.java -> HomeViewModel(repo) as T
        FuelViewModel::class.java -> FuelViewModel(repo) as T
        ExpenseViewModel::class.java -> ExpenseViewModel(repo) as T
        StatsViewModel::class.java -> StatsViewModel(repo) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
