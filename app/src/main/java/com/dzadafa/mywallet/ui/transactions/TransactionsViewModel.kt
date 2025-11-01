package com.dzadafa.mywallet.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.data.Transaction
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TransactionsViewModel : ViewModel() {

    private val db: FirebaseFirestore = Firebase.firestore
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val _allTransactions = MutableLiveData<List<Transaction>>()

    private val _incomeList = MutableLiveData<List<Transaction>>()
    val incomeList: LiveData<List<Transaction>> = _incomeList

    private val _expenseList = MutableLiveData<List<Transaction>>()
    val expenseList: LiveData<List<Transaction>> = _expenseList

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    init {
        loadAllTransactions()
    }

    private fun loadAllTransactions() {
        if (currentUserId == null) {
            _toastMessage.value = "User not logged in"
            return
        }

        val collectionPath = "users/$currentUserId/transactions"

        db.collection(collectionPath)
            .orderBy("date", Query.Direction.DESCENDING) // Newest first
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _toastMessage.value = "Error loading data: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactions = snapshot.toObjects(Transaction::class.java)
                    _allTransactions.value = transactions
                    
                    _incomeList.value = transactions.filter { it.type == "income" }
                    _expenseList.value = transactions.filter { it.type == "expense" }
                }
            }
    }

    fun addTransaction(type: String, description: String, amountStr: String, category: String) {
        if (currentUserId == null) {
            _toastMessage.value = "Error: Not logged in"
            return
        }
        
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
            category = category.replaceFirstChar { it.uppercase() }, // Capitalize category
            date = Timestamp.now()
        )

        viewModelScope.launch {
            try {
                db.collection("users/$currentUserId/transactions")
                    .add(newTransaction)
                    .await() // .await() comes from kotlinx-coroutines-play-services
                _toastMessage.value = "Transaction added!"
            } catch (e: Exception) {
                _toastMessage.value = "Error adding transaction: ${e.message}"
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        if (currentUserId == null || transaction.id == null) {
            _toastMessage.value = "Error: Cannot delete"
            return
        }

        viewModelScope.launch {
            try {
                db.collection("users/$currentUserId/transactions")
                    .document(transaction.id)
                    .delete()
                    .await() // .await() comes from kotlinx-coroutines-play-services
                _toastMessage.value = "Transaction deleted"
            } catch (e: Exception) {
                _toastMessage.value = "Error deleting: ${e.message}"
            }
        }
    }
}
