package com.example.carlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.carlog.data.AppDatabase
import com.example.carlog.data.CarLogRepository
import com.example.carlog.ui.CarLogViewModelFactory
import com.example.carlog.ui.theme.CarLogTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.get(applicationContext)
        val repository = CarLogRepository(db.fuelDao(), db.expenseDao())
        val factory = CarLogViewModelFactory(repository)

        setContent {
            CarLogTheme {
                CarLogApp(factory)
            }
        }
    }
}
