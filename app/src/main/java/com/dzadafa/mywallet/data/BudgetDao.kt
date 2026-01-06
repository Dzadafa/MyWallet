package com.dzadafa.mywallet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month ORDER BY category ASC")
    fun getBudgetsForMonth(year: Int, month: Int): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE year = :year AND month = :month")
    suspend fun getBudgetsForMonthSync(year: Int, month: Int): List<Budget>

    @Query("SELECT * FROM budgets WHERE (year < :year) OR (year = :year AND month < :month) ORDER BY year DESC, month DESC LIMIT 1")
    suspend fun getLatestBudgetBefore(year: Int, month: Int): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<Budget>)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}
