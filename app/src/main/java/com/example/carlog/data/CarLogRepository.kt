package com.example.carlog.data

import kotlinx.coroutines.flow.Flow

class CarLogRepository(
    private val fuelDao: FuelDao,
    private val expenseDao: ExpenseDao
) {
    /** Заправки: новые сверху. */
    val fuelDesc: Flow<List<FuelEntry>> = fuelDao.observeAllDesc()

    /** Заправки: по возрастанию пробега (для статистики). */
    val fuelByOdometer: Flow<List<FuelEntry>> = fuelDao.observeAllByOdometer()

    /** Расходы: новые сверху. */
    val expenses: Flow<List<ExpenseEntry>> = expenseDao.observeAllDesc()

    suspend fun saveFuel(entry: FuelEntry) = fuelDao.upsert(entry)
    suspend fun deleteFuel(entry: FuelEntry) = fuelDao.delete(entry)

    suspend fun saveExpense(entry: ExpenseEntry) = expenseDao.upsert(entry)
    suspend fun deleteExpense(entry: ExpenseEntry) = expenseDao.delete(entry)
}
