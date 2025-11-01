package com.dzadafa.mywallet.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.utils.Utils 
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import java.util.Calendar
import java.util.Date


enum class TimeFilter { ALL_TIME, THIS_MONTH, THIS_YEAR }

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = Firebase.firestore
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    
    private val _allTransactions = MutableLiveData<List<Transaction>>()

    
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
        loadAllTransactions()
        _allTransactions.observeForever { updateDashboardData() }
        _timeFilter.observeForever { updateDashboardData() }
    }

    private fun loadAllTransactions() {
        if (currentUserId == null) return
        val path = "users/$currentUserId/transactions"
        db.collection(path).addSnapshotListener { snapshot, _ ->
            _allTransactions.value = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
        }
    }

    fun setFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    private fun updateDashboardData() {
        val allTransactions = _allTransactions.value ?: emptyList()
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
            
            it.date.toDate().after(startTime) || it.date.toDate() == startTime
        }
    }
}
