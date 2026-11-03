package com.example.carlog.ui.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.data.FuelEntry
import com.example.carlog.ui.CURRENCY
import com.example.carlog.ui.fmtConsumption
import com.example.carlog.ui.fmtDate
import com.example.carlog.ui.fmtLiters
import com.example.carlog.ui.fmtMoney

/**
 * Мгновенный расход конкретной заправки: литры этой заправки
 * на расстояние от предыдущей заправки (по пробегу).
 */
private fun instantConsumption(asc: List<FuelEntry>, entry: FuelEntry): Double? {
    if (!entry.fullTank) return null
    val prev = asc.lastOrNull { it.odometer < entry.odometer } ?: return null
    val dist = entry.odometer - prev.odometer
    return if (dist > 0) entry.liters / dist * 100.0 else null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelListScreen(
    vm: FuelViewModel,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val entries by vm.entries.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заправку")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Заправок пока нет.\nНажмите +, чтобы добавить первую.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val asc = remember(entries) { entries.sortedBy { it.odometer } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    val consumption = instantConsumption(asc, entry)

                    ElevatedCard(
                        onClick = { onEdit(entry.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    fmtDate(entry.date),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    fmtMoney(entry.totalCost),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Пробег: ${entry.odometer} км · " +
                                    "${fmtLiters(entry.liters)} × " +
                                    "%.2f $CURRENCY/л".format(entry.pricePerLiter),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (entry.station.isNotBlank()) {
                                Text(
                                    entry.station,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (consumption != null) {
                                Text(
                                    "Расход: ${fmtConsumption(consumption)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (!entry.fullTank) {
                                Text(
                                    "Неполный бак — не участвует в расчёте среднего расхода",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (entry.note.isNotBlank()) {
                                Text(
                                    entry.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
