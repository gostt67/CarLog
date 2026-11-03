package com.example.carlog.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.ui.SectionCard
import com.example.carlog.ui.StatCard
import com.example.carlog.ui.fmtConsumption
import com.example.carlog.ui.fmtDate
import com.example.carlog.ui.fmtLiters
import com.example.carlog.ui.fmtMoney

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onAddFuel: () -> Unit,
    onAddExpense: () -> Unit
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Car Log", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Учёт заправок и расходов на автомобиль",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Средний расход",
                value = state.avgConsumption?.let { fmtConsumption(it) } ?: "—",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Траты за месяц",
                value = fmtMoney(state.monthFuelCost + state.monthExpenseCost),
                modifier = Modifier.weight(1f)
            )
        }

        SectionCard(title = "Последняя заправка") {
            val last = state.lastFuel
            if (last == null) {
                Text(
                    "Заправок пока нет — добавьте первую!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    fmtDate(last.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${fmtLiters(last.liters)} × %.2f = %s".format(
                        last.pricePerLiter,
                        fmtMoney(last.totalCost)
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Пробег: ${last.odometer} км",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SectionCard(title = "Всего записей") {
            Text(
                "Заправок: ${state.fuelCount} · Расходов: ${state.expenseCount}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAddFuel, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.LocalGasStation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Заправка")
            }
            OutlinedButton(onClick = onAddExpense, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Расход")
            }
        }
    }
}
