package com.example.carlog.ui.fuel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalGasStation
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.data.FuelEntry
import com.example.carlog.ui.AppCard
import com.example.carlog.ui.CURRENCY
import com.example.carlog.ui.EmptyState
import com.example.carlog.ui.IconBadge
import com.example.carlog.ui.InfoChip
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
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = scheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = scheme.primary,
                contentColor = scheme.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заправку")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.LocalGasStation,
                title = "Заправок пока нет",
                subtitle = "Нажмите «+», чтобы добавить первую.",
                modifier = Modifier.padding(padding)
            )
        } else {
            val asc = remember(entries) { entries.sortedBy { it.odometer } }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    val consumption = instantConsumption(asc, entry)

                    AppCard(
                        onClick = { onEdit(entry.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                        fmtDate(entry.date),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (entry.station.isNotBlank()) {
                                        Text(
                                            entry.station,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = scheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    fmtMoney(entry.totalCost),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.primary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoChip(fmtLiters(entry.liters))
                                InfoChip("%.2f $CURRENCY/л".format(entry.pricePerLiter))
                                InfoChip("${entry.odometer} км")
                            }

                            if (consumption != null || !entry.fullTank) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (consumption != null) {
                                        InfoChip(
                                            text = "Расход: ${fmtConsumption(consumption)}",
                                            container = scheme.tertiaryContainer,
                                            content = scheme.onTertiaryContainer
                                        )
                                    }
                                    if (!entry.fullTank) {
                                        InfoChip(
                                            text = "Неполный бак",
                                            container = scheme.secondaryContainer,
                                            content = scheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            if (entry.note.isNotBlank()) {
                                Text(
                                    entry.note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
