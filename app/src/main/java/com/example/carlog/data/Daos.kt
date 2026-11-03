package com.example.carlog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelDao {

    /** Все заправки, новые сверху (для списка). */
    @Query("SELECT * FROM fuel_entries ORDER BY date DESC, odometer DESC")
    fun observeAllDesc(): Flow<List<FuelEntry>>

    /** Все заправки по возрастанию пробега (для расчёта статистики). */
    @Query("SELECT * FROM fuel_entries ORDER BY odometer ASC")
    fun observeAllByOdometer(): Flow<List<FuelEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: FuelEntry): Long

    @Delete
    suspend fun delete(entry: FuelEntry)
}

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expense_entries ORDER BY date DESC, id DESC")
    fun observeAllDesc(): Flow<List<ExpenseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: ExpenseEntry): Long

    @Delete
    suspend fun delete(entry: ExpenseEntry)
}
