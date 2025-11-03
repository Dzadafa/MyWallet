package com.dzadafa.mywallet.widget

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.databinding.ActivityWidgetAddTransactionBinding
import kotlinx.coroutines.launch
import java.util.Date

class WidgetAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetAddTransactionBinding

    private val viewModel: WidgetAddViewModel by viewModels {
        WidgetAddViewModelFactory(
            (application as MyWalletApplication).transactionRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Add Transaction via Widget"

        binding.btnWidgetSaveTransaction.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val description = binding.etWidgetDescription.text.toString()
        val amountStr = binding.etWidgetAmount.text.toString()
        val category = binding.etWidgetCategory.text.toString()
        val selectedTypeId = binding.rgWidgetType.checkedRadioButtonId
        val type = if (selectedTypeId == R.id.rb_widget_income) "income" else "expense"
        val amount = amountStr.toDoubleOrNull()

        if (description.isBlank() || category.isBlank() || amount == null || amount <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val newTransaction = Transaction(
            type = type,
            description = description,
            amount = amount,
            category = category.replaceFirstChar { it.uppercase() },
            date = Date() // Use current date for quick add
        )

        binding.btnWidgetSaveTransaction.isEnabled = false
        binding.btnWidgetSaveTransaction.text = "Saving..."

        viewModel.addTransaction(newTransaction)
        
        Toast.makeText(applicationContext, "Transaction added!", Toast.LENGTH_SHORT).show()
        finish()
    }
}

// --- ViewModel and Factory for this Activity ---

class WidgetAddViewModel(private val repository: TransactionRepository) : ViewModel() {
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }
}

class WidgetAddViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WidgetAddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WidgetAddViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
