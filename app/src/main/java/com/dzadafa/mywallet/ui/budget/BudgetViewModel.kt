package com.dzadafa.mywallet.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
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

    val budgetList: LiveData<List<BudgetWithUsage>> = combine(
        budgetRepository.allBudgets,
        transactionRepository.allTransactions
    ) { budgets, transactions ->

        val (filterType, year, month) = FilterManager.getFilterState(getApplication())
        val cal = Calendar.getInstance()

        val filteredTransactions = transactions.filter { txn ->
            if (txn.type != "expense") return@filter false

            cal.time = txn.date
            val txnYear = cal.get(Calendar.YEAR)
            val txnMonth = cal.get(Calendar.MONTH)

            when (filterType) {
                FilterManager.FilterType.ALL_TIME -> true
                FilterManager.FilterType.THIS_YEAR -> txnYear == year
                else -> txnYear == year && txnMonth == month 

            }
        }

        budgets.map { budget ->
            val spent = filteredTransactions
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

    fun insert(category: String, limit: Double) {
        if (category.isBlank()) {
            _toastMessage.value = "Category name cannot be empty"
            return
        }
        viewModelScope.launch {
            try {
                val formattedCategory = category.trim().replaceFirstChar { it.uppercase() }
                budgetRepository.insert(Budget(category = formattedCategory, limitAmount = limit))
                _toastMessage.postValue("Category added")
            } catch (e: Exception) {
                _toastMessage.postValue("Category might already exist")
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
            _toastMessage.postValue("Deleted")
        }
    }
}
