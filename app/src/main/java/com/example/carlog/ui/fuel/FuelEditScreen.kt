package com.example.carlog.ui.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.data.FuelEntry
import com.example.carlog.ui.CURRENCY
import com.example.carlog.ui.fmtDate
import com.example.carlog.ui.fmtMoney

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelEditScreen(
    vm: FuelViewModel,
    entryId: Long,           // -1 = новая заправка
    onBack: () -> Unit
) {
    val entries by vm.entries.collectAsStateWithLifecycle()
    val existing = remember(entries, entryId) { entries.find { it.id == entryId } }

    var initialized by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf(existing?.date ?: System.currentTimeMillis()) }
    var odometer by remember { mutableStateOf(existing?.odometer?.toString() ?: "") }
    var liters by remember { mutableStateOf(existing?.liters?.toString()?.replace('.', ',') ?: "") }
    var price by remember { mutableStateOf(existing?.pricePerLiter?.toString()?.replace('.', ',') ?: "") }
    var fullTank by remember { mutableStateOf(existing?.fullTank ?: true) }
    var station by remember { mutableStateOf(existing?.station ?: "") }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Список грузится из БД асинхронно — заполняем поля, когда запись появилась.
    LaunchedEffect(existing) {
        if (!initialized && existing != null) {
            date = existing.date
            odometer = existing.odometer.toString()
            liters = existing.liters.toString().replace('.', ',')
            price = existing.pricePerLiter.toString().replace('.', ',')
            fullTank = existing.fullTank
            station = existing.station
            note = existing.note
            initialized = true
        }
    }

    val odometerV = odometer.toIntOrNull()
    val litersV = liters.replace(',', '.').toDoubleOrNull()
    val priceV = price.replace(',', '.').toDoubleOrNull()
    val total = if (litersV != null && priceV != null) litersV * priceV else null
    val canSave = odometerV != null && odometerV > 0 &&
        litersV != null && litersV > 0 &&
        priceV != null && priceV > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Новая заправка" else "Изменить заправку") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = {
                            vm.delete(existing)
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Дата: ${fmtDate(date)}")
            }

            OutlinedTextField(
                value = odometer,
                onValueChange = { odometer = it.filter(Char::isDigit) },
                label = { Text("Пробег, км") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = liters,
                onValueChange = { liters = it },
                label = { Text("Литры") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Цена за литр, $CURRENCY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Итого: ${total?.let { fmtMoney(it) } ?: "—"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = fullTank, onCheckedChange = { fullTank = it })
                Text("Полный бак")
            }

            OutlinedTextField(
                value = station,
                onValueChange = { station = it },
                label = { Text("АЗС (необязательно)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Заметка (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    vm.save(
                        FuelEntry(
                            id = existing?.id ?: 0,
                            date = date,
                            odometer = odometerV!!,
                            liters = litersV!!,
                            pricePerLiter = priceV!!,
                            totalCost = total!!,
                            fullTank = fullTank,
                            station = station.trim(),
                            note = note.trim()
                        )
                    )
                    onBack()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { date = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
