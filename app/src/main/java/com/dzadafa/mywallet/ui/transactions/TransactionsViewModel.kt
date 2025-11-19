package com.dzadafa.mywallet.ui.transactions

import android.app.Application
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.FilterManager
import com.dzadafa.mywallet.MainActivity
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.utils.Utils
import kotlinx.coroutines.launch
import java.util.Date

class TransactionsViewModel(
    private val repository: TransactionRepository,
    application: Application
) : AndroidViewModel(application) {

    private val rawTransactions: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()
    private val _filterTrigger = MutableLiveData(Unit)

    val incomeList: LiveData<List<Transaction>> = rawTransactions.map { transactions ->
        filterAndSort(transactions, TransactionType.INCOME)
    }

    val expenseList: LiveData<List<Transaction>> = rawTransactions.map { transactions ->
        filterAndSort(transactions, TransactionType.EXPENSE)
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        _filterTrigger.observeForever {
            // Observing this trigger forces the map transformations (incomeList, expenseList) to re-run
        }
    }

    private fun filterAndSort(transactions: List<Transaction>, type: TransactionType): List<Transaction> {
        val filteredByDate = FilterManager.filterTransactions(getApplication(), transactions)
        return filteredByDate.filter { it.type == type.name.lowercase() }
    }

    fun forceFilterUpdate() {
        // Trigger the LiveData map transformation to re-run with the new global filter
        _filterTrigger.value = Unit
    }

    fun addTransaction(type: String, description: String, amountStr: String, category: String, date: Date) {
        if (description.isBlank()) {
            _toastMessage.value = "Please enter a description"
            return
        }
        if (category.isBlank()) {
            _toastMessage.value = "Please enter a category"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _toastMessage.value = "Please enter a valid amount"
            return
        }

        val newTransaction = Transaction(
            type = type,
            description = description,
            amount = amount,
            category = category.replaceFirstChar { it.uppercase() },
            date = date
        )

        viewModelScope.launch {
            repository.insert(newTransaction)
            _toastMessage.postValue("Transaction added!")
            sendTransactionNotification(newTransaction)
        }
    }

    private fun sendTransactionNotification(transaction: Transaction) {
        val context = getApplication<Application>().applicationContext
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val amountString = Utils.formatAsRupiah(transaction.amount)
        val title = if (transaction.type == "income") "Income Added" else "Expense Added"
        val text = "${transaction.description}: $amountString"

        val builder = NotificationCompat.Builder(context, "TRANSACTION_CHANNEL")
            .setSmallIcon(R.drawable.ic_transactions)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
            _toastMessage.postValue("Transaction deleted")
        }
    }

    private enum class TransactionType { INCOME, EXPENSE }
}
