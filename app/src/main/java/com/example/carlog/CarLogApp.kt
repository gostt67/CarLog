package com.example.carlog

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carlog.ui.CarLogViewModelFactory
import com.example.carlog.ui.expenses.ExpenseEditScreen
import com.example.carlog.ui.expenses.ExpenseListScreen
import com.example.carlog.ui.expenses.ExpenseViewModel
import com.example.carlog.ui.fuel.FuelEditScreen
import com.example.carlog.ui.fuel.FuelListScreen
import com.example.carlog.ui.fuel.FuelViewModel
import com.example.carlog.ui.home.HomeScreen
import com.example.carlog.ui.home.HomeViewModel
import com.example.carlog.ui.stats.StatsScreen
import com.example.carlog.ui.stats.StatsViewModel

private data class TopDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val topDestinations = listOf(
    TopDestination("home", "Главная", Icons.Filled.Home),
    TopDestination("fuel", "Заправки", Icons.Filled.LocalGasStation),
    TopDestination("expenses", "Расходы", Icons.Filled.Receipt),
    TopDestination("stats", "Статистика", Icons.Filled.BarChart)
)

@Composable
fun CarLogApp(factory: CarLogViewModelFactory) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val topRoutes = topDestinations.map { it.route }

    Scaffold(
        bottomBar = {
            if (currentRoute in topRoutes) {
                NavigationBar {
                    topDestinations.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(
                    vm = vm,
                    onAddFuel = { navController.navigate("fuel_edit/-1") },
                    onAddExpense = { navController.navigate("expense_edit/-1") }
                )
            }

            composable("fuel") {
                val vm: FuelViewModel = viewModel(factory = factory)
                FuelListScreen(
                    vm = vm,
                    onAdd = { navController.navigate("fuel_edit/-1") },
                    onEdit = { id -> navController.navigate("fuel_edit/$id") }
                )
            }

            composable(
                route = "fuel_edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val vm: FuelViewModel = viewModel(factory = factory)
                FuelEditScreen(
                    vm = vm,
                    entryId = entry.arguments?.getLong("id") ?: -1L,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("expenses") {
                val vm: ExpenseViewModel = viewModel(factory = factory)
                ExpenseListScreen(
                    vm = vm,
                    onAdd = { navController.navigate("expense_edit/-1") },
                    onEdit = { id -> navController.navigate("expense_edit/$id") }
                )
            }

            composable(
                route = "expense_edit/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val vm: ExpenseViewModel = viewModel(factory = factory)
                ExpenseEditScreen(
                    vm = vm,
                    entryId = entry.arguments?.getLong("id") ?: -1L,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("stats") {
                val vm: StatsViewModel = viewModel(factory = factory)
                StatsScreen(vm)
            }
        }
    }
}
