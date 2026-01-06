package com.dzadafa.mywallet.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.FilterManager
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.data.BudgetRepository
import com.dzadafa.mywallet.data.TransactionRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetWithUsage(
    val budget: Budget,
    val spent: Double,
    val progressPercent: Int,
    val isOverBudget: Boolean
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private fun getFilterYearMonth(): Pair<Int, Int> {
        val (type, year, month) = FilterManager.getFilterState(getApplication())

        val cal = Calendar.getInstance()
        return if (type == FilterManager.FilterType.THIS_MONTH) {

             Pair(year, month)
        } else {

             Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }
    }

    private val _refreshTrigger = MutableLiveData(Unit)

    val budgetList: LiveData<List<BudgetWithUsage>> = _refreshTrigger.switchMap {
        val (year, month) = getFilterYearMonth()

        viewModelScope.launch {
            budgetRepository.ensureBudgetsExistForMonth(year, month)
        }

        combine(
            budgetRepository.getBudgetsForMonth(year, month),
            transactionRepository.allTransactions
        ) { budgets, transactions ->

            val monthlyTransactions = transactions.filter { txn ->
                if (txn.type != "expense") return@filter false
                val cal = Calendar.getInstance()
                cal.time = txn.date
                cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
            }

            budgets.map { budget ->
                val spent = monthlyTransactions
                    .filter { it.category.equals(budget.category, ignoreCase = true) }
                    .sumOf { it.amount }

                val percent = if (budget.limitAmount > 0) {
                    (spent / budget.limitAmount * 100).toInt()
                } else 0

                BudgetWithUsage(
                    budget = budget,
                    spent = spent,
                    progressPercent = percent,
                    isOverBudget = spent > budget.limitAmount
                )
            }
        }.asLiveData()
    }

    init {
        refresh()
    }

    fun refresh() {
        _refreshTrigger.value = Unit
    }

    fun insert(category: String, limit: Double) {
        if (category.isBlank()) return

        val (year, month) = getFilterYearMonth()
        val formattedCategory = category.trim().replaceFirstChar { it.uppercase() }

        viewModelScope.launch {
            try {
                budgetRepository.insert(
                    Budget(
                        category = formattedCategory, 
                        limitAmount = limit,
                        year = year, 
                        month = month
                    )
                )
                _toastMessage.postValue("Category added for this month")
                refresh()
            } catch (e: Exception) {
                _toastMessage.postValue("Category already exists")
            }
        }
    }

    fun update(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.update(budget)
            _toastMessage.postValue("Updated")
        }
    }

    fun delete(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.delete(budget)
            _toastMessage.postValue("Deleted from this month")
        }
    }
}
