package com.example.carlog.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carlog.ui.CURRENCY
import com.example.carlog.ui.EmptyState
import com.example.carlog.ui.HeroCard
import com.example.carlog.ui.HeroStat
import com.example.carlog.ui.MonthlyStackedBarChart
import com.example.carlog.ui.PriceLineChart
import com.example.carlog.ui.SectionCard
import com.example.carlog.ui.StatCard
import com.example.carlog.ui.fmtConsumption
import com.example.carlog.ui.fmtMoney

@Composable
fun StatsScreen(vm: StatsViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()

    if (!state.hasData) {
        EmptyState(
            icon = Icons.Filled.BarChart,
            title = "Пока нет данных",
            subtitle = "Добавьте заправки и расходы — здесь появится статистика."
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Статистика", style = MaterialTheme.typography.headlineMedium)

        HeroCard(
            title = "Всего потрачено",
            value = fmtMoney(state.totalFuelCost + state.totalOtherCost)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                HeroStat("Топливо", fmtMoney(state.totalFuelCost))
                HeroStat("Прочее", fmtMoney(state.totalOtherCost))
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
                title = "Стоимость 1 км",
                value = state.costPerKm?.let { "%.2f $CURRENCY".format(it) } ?: "—",
                icon = Icons.Filled.AccountBalanceWallet,
                modifier = Modifier.weight(1f)
            )
        }

        StatCard(
            title = "Учтённый пробег",
            value = "${state.totalDistance} км",
            icon = Icons.Filled.Timeline,
            modifier = Modifier.fillMaxWidth()
        )

        SectionCard(title = "Расходы по месяцам") {
            MonthlyStackedBarChart(
                data = state.monthly,
                modifier = Modifier.fillMaxWidth()
            )
        }

        SectionCard(title = "Цена топлива, $CURRENCY/л") {
            PriceLineChart(
                points = state.priceHistory,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            "Средний расход считается по заправкам «до полного бака». " +
                "Неполные заправки учитываются в тратах, но не в расходе топлива.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
