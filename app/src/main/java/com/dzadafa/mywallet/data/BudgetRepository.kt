package com.dzadafa.mywallet.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getBudgetsForMonth(year: Int, month: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(year, month)
    }

    suspend fun ensureBudgetsExistForMonth(year: Int, month: Int) {
        withContext(Dispatchers.IO) {
            val currentBudgets = budgetDao.getBudgetsForMonthSync(year, month)
            if (currentBudgets.isEmpty()) {

                val lastBudgetEntry = budgetDao.getLatestBudgetBefore(year, month)

                if (lastBudgetEntry != null) {

                    val prevBudgets = budgetDao.getBudgetsForMonthSync(lastBudgetEntry.year, lastBudgetEntry.month)

                    val newBudgets = prevBudgets.map { old ->
                        old.copy(id = 0, year = year, month = month) 

                    }
                    budgetDao.insertAll(newBudgets)
                }
            }
        }
    }

    suspend fun insert(budget: Budget) = budgetDao.insert(budget)
    suspend fun update(budget: Budget) = budgetDao.update(budget)
    suspend fun delete(budget: Budget) = budgetDao.delete(budget)
}
