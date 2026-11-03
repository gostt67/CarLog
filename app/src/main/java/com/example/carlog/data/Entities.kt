package com.example.carlog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Заправка. */
@Entity(tableName = "fuel_entries")
data class FuelEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,              // миллисекунды (UTC, полночь выбранной даты)
    val odometer: Int,           // пробег, км
    val liters: Double,          // залито литров
    val pricePerLiter: Double,   // цена за литр
    val totalCost: Double,       // итоговая сумма
    val fullTank: Boolean = true, // полный бак — нужно для расчёта расхода
    val station: String = "",    // АЗС (необязательно)
    val note: String = ""        // заметка (необязательно)
)

/** Прочий расход на автомобиль. */
@Entity(tableName = "expense_entries")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val category: String,
    val amount: Double,
    val note: String = ""
)

object ExpenseCategories {
    val all = listOf(
        "ТО / обслуживание",
        "Ремонт",
        "Страховка",
        "Шины",
        "Мойка",
        "Парковка",
        "Штрафы",
        "Прочее"
    )
}
