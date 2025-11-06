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
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.utils.Utils
import com.dzadafa.mywallet.widget.StatsWidgetProvider
import java.util.Calendar
import java.util.Date

enum class TimeFilter { ALL_TIME, THIS_MONTH, THIS_YEAR }

class DashboardViewModel(
    private val repository: TransactionRepository,
    application: Application
) : AndroidViewModel(application) {

    private val allTransactions: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()
    private val _timeFilter = MutableLiveData(TimeFilter.ALL_TIME)

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
        _timeFilter.observeForever { updateDashboardData() }
    }

    fun setFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    private fun updateDashboardData() {
        val allTransactions = allTransactions.value ?: emptyList()
        val filter = _timeFilter.value ?: TimeFilter.ALL_TIME

        val totalIncomeAllTime = allTransactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpenseAllTime = allTransactions.filter { it.type == "expense" }.sumOf { it.amount }
        val currentBalanceValue = totalIncomeAllTime - totalExpenseAllTime
        _currentBalance.value = currentBalanceValue

        val filteredTransactions = getFilteredTransactions(allTransactions, filter)

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

    private fun getFilteredTransactions(transactions: List<Transaction>, filter: TimeFilter): List<Transaction> {
        if (filter == TimeFilter.ALL_TIME) {
            return transactions
        }

        val cal = Calendar.getInstance()
        val now = cal.time

        val startTime: Date = when (filter) {
            TimeFilter.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                cal.time
            }
            TimeFilter.THIS_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
                cal.time
            }
            TimeFilter.ALL_TIME -> now
        }

        return transactions.filter {
            it.date.after(startTime) || it.date == startTime
        }
    }
}
