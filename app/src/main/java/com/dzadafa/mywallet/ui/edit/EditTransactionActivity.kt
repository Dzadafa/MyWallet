package com.dzadafa.mywallet.ui.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.data.BudgetRepository
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.databinding.ActivityEditTransactionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTransactionBinding
    private val selectedDate = Calendar.getInstance()
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private var transactionId: Int = 0
    private var currentTransaction: Transaction? = null

    private val viewModel: EditTransactionViewModel by viewModels {
        EditTransactionViewModelFactory(
            (application as MyWalletApplication).transactionRepository,
            (application as MyWalletApplication).budgetRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        transactionId = intent.getIntExtra("TRANSACTION_ID", 0)
        if (transactionId == 0) {
            Toast.makeText(this, "Error: Invalid transaction ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupDatePicker()

        viewModel.loadTransaction(transactionId)

        viewModel.transaction.observe(this) { transaction ->
            if (transaction != null) {
                currentTransaction = transaction
                populateUi(transaction)
            }
        }

        viewModel.allBudgets.observe(this) { budgets ->
            val categories = budgets.map { it.category }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
            binding.etCategory.setAdapter(adapter)

            currentTransaction?.let { txn ->
                binding.etCategory.setText(txn.category, false) 

            }
        }

        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }

        binding.btnDeleteTransaction.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                deleteItemAndFinish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItemAndFinish() {
        currentTransaction?.let {
            viewModel.deleteTransaction(it)
            Toast.makeText(applicationContext, "Transaction deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun populateUi(txn: Transaction) {
        binding.etDescription.setText(txn.description)
        binding.etAmount.setText(txn.amount.toString())
        binding.etCategory.setText(txn.category, false) 

        if (txn.type == "income") {
            binding.rbIncome.isChecked = true
        } else {
            binding.rbExpense.isChecked = true
        }

        selectedDate.time = txn.date
        updateDateEditText()
    }

    private fun setupDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateEditText()
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateEditText() {
        binding.etDate.setText(displayDateFormat.format(selectedDate.time))
    }

    private fun saveChanges() {
        val description = binding.etDescription.text.toString()
        val amountStr = binding.etAmount.text.toString()
        val category = binding.etCategory.text.toString()
        val type = if (binding.rbIncome.isChecked) "income" else "expense"
        val amount = amountStr.toDoubleOrNull()
        val date = selectedDate.time

        if (description.isBlank() || category.isBlank() || amount == null || amount <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTransaction = Transaction(
            id = transactionId,
            type = type,
            description = description,
            amount = amount,
            category = category, 

            date = date
        )

        binding.btnSaveChanges.isEnabled = false
        binding.btnSaveChanges.text = "Saving..."

        viewModel.saveChanges(updatedTransaction)

        Toast.makeText(applicationContext, "Changes saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class EditTransactionViewModel(
    private val repository: TransactionRepository,
    budgetRepository: BudgetRepository
) : ViewModel() {

    private val _transaction = MutableLiveData<Transaction?>()
    val transaction: LiveData<Transaction?> = _transaction

    val allBudgets: LiveData<List<Budget>> = budgetRepository.allBudgets.asLiveData()

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            _transaction.value = repository.getTransactionById(id)
        }
    }

    fun saveChanges(transaction: Transaction) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}

class EditTransactionViewModelFactory(
    private val repository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTransactionViewModel(repository, budgetRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
