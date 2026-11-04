package com.example.carlog.ui.expenses

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocalCarWash
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.ui.AppCard
import com.example.carlog.ui.EmptyState
import com.example.carlog.ui.IconBadge
import com.example.carlog.ui.fmtDate
import com.example.carlog.ui.fmtMoney

/** Иконка по названию категории расхода. */
private fun categoryIcon(category: String): ImageVector = when {
    category.startsWith("ТО") -> Icons.Filled.Build
    category.startsWith("Ремонт") -> Icons.Filled.Settings
    category.startsWith("Страх") -> Icons.Filled.Shield
    category.startsWith("Шин") -> Icons.Filled.Circle
    category.startsWith("Мойк") -> Icons.Filled.LocalCarWash
    category.startsWith("Парк") -> Icons.Filled.LocalParking
    category.startsWith("Штраф") -> Icons.Filled.Warning
    else -> Icons.Filled.Receipt
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreen(
    vm: ExpenseViewModel,
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
                Icon(Icons.Default.Add, contentDescription = "Добавить расход")
            }
        }
    ) { padding ->
        if (entries.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Receipt,
                title = "Расходов пока нет",
                subtitle = "Нажмите «+», чтобы добавить ТО, страховку, мойку и другие траты.",
                modifier = Modifier.padding(padding)
            )
        } else {
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
                    val isFine = entry.category.startsWith("Штраф")

                    AppCard(
                        onClick = { onEdit(entry.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconBadge(
                                icon = categoryIcon(entry.category),
                                container = if (isFine) scheme.errorContainer else scheme.primaryContainer,
                                content = if (isFine) scheme.onErrorContainer else scheme.onPrimaryContainer
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    entry.category,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    fmtDate(entry.date),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurfaceVariant
                                )
                                if (entry.note.isNotBlank()) {
                                    Text(
                                        entry.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = scheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                fmtMoney(entry.amount),
                                style = MaterialTheme.typography.titleMedium,
                                color = scheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
