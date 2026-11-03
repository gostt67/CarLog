package com.example.carlog.ui.fuel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlog.data.CarLogRepository
import com.example.carlog.data.FuelEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FuelViewModel(private val repo: CarLogRepository) : ViewModel() {

    val entries: StateFlow<List<FuelEntry>> =
        repo.fuelDesc.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun save(entry: FuelEntry) {
        viewModelScope.launch { repo.saveFuel(entry) }
    }

    fun delete(entry: FuelEntry) {
        viewModelScope.launch { repo.deleteFuel(entry) }
    }
}
