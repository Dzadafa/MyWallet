package com.dzadafa.mywallet.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import kotlinx.coroutines.launch
import java.util.Date

class TransactionsViewModel(private val repository: TransactionRepository) : ViewModel() {

    val incomeList: LiveData<List<Transaction>> = repository.allTransactions
        .asLiveData()
        .mapTransactions(TransactionType.INCOME)

    val expenseList: LiveData<List<Transaction>> = repository.allTransactions
        .asLiveData()
        .mapTransactions(TransactionType.EXPENSE)

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

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
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
            _toastMessage.postValue("Transaction deleted")
        }
    }

    // --- HELPER FUNCTIONS MOVED INSIDE THE CLASS ---
    private enum class TransactionType { INCOME, EXPENSE }

    private fun LiveData<List<Transaction>>.mapTransactions(type: TransactionType): LiveData<List<Transaction>> {
        val result = MutableLiveData<List<Transaction>>()
        this.observeForever { transactions ->
            result.value = transactions.filter { it.type == type.name.lowercase() }
        }
        return result
    }
}
