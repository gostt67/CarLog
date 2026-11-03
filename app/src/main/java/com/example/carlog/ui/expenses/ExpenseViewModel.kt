package com.example.carlog.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlog.data.CarLogRepository
import com.example.carlog.data.ExpenseEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repo: CarLogRepository) : ViewModel() {

    val entries: StateFlow<List<ExpenseEntry>> =
        repo.expenses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun save(entry: ExpenseEntry) {
        viewModelScope.launch { repo.saveExpense(entry) }
    }

    fun delete(entry: ExpenseEntry) {
        viewModelScope.launch { repo.deleteExpense(entry) }
    }
}
