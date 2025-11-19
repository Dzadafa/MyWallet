package com.dzadafa.mywallet.ui.dashboard

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.dzadafa.mywallet.FilterManager
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.utils.Utils
import com.dzadafa.mywallet.widget.StatsWidgetProvider
import java.util.Calendar
import java.util.Date

class DashboardViewModel(
    private val repository: TransactionRepository,
    application: Application
) : AndroidViewModel(application) {

    private val allTransactions: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()

    private val _currentBalance = MutableLiveData<Double>()
    val currentBalance: LiveData<Double> = _currentBalance

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _expenseBreakdown = MutableLiveData<Map<String, Double>>()
    val expenseBreakdown: LiveData<Map<String, Double>> = _expenseBreakdown

    init {
        allTransactions.observeForever { updateDashboardData() }
    }

    fun updateDashboardData() {
        val allTransactions = allTransactions.value ?: emptyList()

        val totalIncomeAllTime = allTransactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpenseAllTime = allTransactions.filter { it.type == "expense" }.sumOf { it.amount }
        val currentBalanceValue = totalIncomeAllTime - totalExpenseAllTime
        _currentBalance.value = currentBalanceValue

        val filteredTransactions = FilterManager.filterTransactions(getApplication(), allTransactions)

        val filteredIncomeValue = filteredTransactions
            .filter { it.type == "income" }
            .sumOf { it.amount }
        _totalIncome.value = filteredIncomeValue

        val filteredExpensesList = filteredTransactions.filter { it.type == "expense" }
        val filteredExpenseValue = filteredExpensesList.sumOf { it.amount }
        _totalExpense.value = filteredExpenseValue

        _expenseBreakdown.value = filteredExpensesList
            .groupBy { it.category }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }

        saveDataToPreferences(currentBalanceValue, filteredIncomeValue, filteredExpenseValue)
    }

    private fun saveDataToPreferences(balance: Double, income: Double, expense: Double) {
        val prefs = getApplication<Application>().getSharedPreferences(
            "MyWalletPrefs",
            Context.MODE_PRIVATE
        )
        with(prefs.edit()) {
            putString("BALANCE", Utils.formatAsRupiah(balance))
            putString("INCOME", Utils.formatAsRupiah(income))
            putString("EXPENSE", Utils.formatAsRupiah(expense))
            apply()
        }
        
        notifyStatsWidgetDataChanged()
    }

    private fun notifyStatsWidgetDataChanged() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, StatsWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(context.packageName.let { ComponentName(it, StatsWidgetProvider::class.java.name) })
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
