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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.ui.AppCard
import com.example.carlog.ui.CURRENCY
import com.example.carlog.ui.HeroCard
import com.example.carlog.ui.HeroStat
import com.example.carlog.ui.IconBadge
import com.example.carlog.ui.InfoChip
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
    val scheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Шапка
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconBadge(
                icon = Icons.Filled.DirectionsCar,
                container = scheme.primary,
                content = scheme.onPrimary,
                size = 48.dp
            )
            Column(Modifier.weight(1f)) {
                Text("Car Log", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Учёт заправок и расходов на автомобиль",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant
                )
            }
        }

        // Главное число: траты за месяц
        HeroCard(
            title = "Траты за месяц",
            value = fmtMoney(state.monthFuelCost + state.monthExpenseCost)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                HeroStat("Топливо", fmtMoney(state.monthFuelCost))
                HeroStat("Прочее", fmtMoney(state.monthExpenseCost))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Средний расход",
                value = state.avgConsumption?.let { fmtConsumption(it) } ?: "—",
                icon = Icons.Filled.Speed,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Записей в журнале",
                value = "${state.fuelCount + state.expenseCount}",
                icon = Icons.Filled.History,
                modifier = Modifier.weight(1f)
            )
        }

        // Последняя заправка
        val last = state.lastFuel
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconBadge(icon = Icons.Filled.LocalGasStation)
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Последняя заправка",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (last != null) {
                            Text(
                                fmtDate(last.date),
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    }
                    if (last != null) {
                        Text(
                            fmtMoney(last.totalCost),
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.primary
                        )
                    }
                }
                if (last == null) {
                    Text(
                        "Заправок пока нет — добавьте первую!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(fmtLiters(last.liters))
                        InfoChip("%.2f $CURRENCY/л".format(last.pricePerLiter))
                        InfoChip("${last.odometer} км")
                    }
                }
            }
        }

        // Быстрые действия
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onAddFuel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Default.LocalGasStation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Заправка")
            }
            FilledTonalButton(
                onClick = onAddExpense,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Расход")
            }
        }
    }
}
