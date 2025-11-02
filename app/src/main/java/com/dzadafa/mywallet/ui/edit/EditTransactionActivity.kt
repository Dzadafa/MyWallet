package com.dzadafa.mywallet.ui.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.databinding.ActivityEditTransactionBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTransactionBinding
    private val db: FirebaseFirestore = Firebase.firestore
    private var transactionId: String? = null
    private var currentTransaction: Transaction? = null
    private val selectedDate = Calendar.getInstance()
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        transactionId = intent.getStringExtra("TRANSACTION_ID")
        if (transactionId == null) {
            Toast.makeText(this, "Error: No transaction ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadTransactionData()
        setupDatePicker()

        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadTransactionData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || transactionId == null) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val doc = db.collection("users/$userId/transactions")
                    .document(transactionId!!)
                    .get()
                    .await()
                
                currentTransaction = doc.toObject(Transaction::class.java)
                
                withContext(Dispatchers.Main) {
                    populateUi()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditTransactionActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateUi() {
        currentTransaction?.let { txn ->
            binding.etDescription.setText(txn.description)
            binding.etAmount.setText(txn.amount.toString())
            binding.etCategory.setText(txn.category)
            
            if (txn.type == "income") {
                binding.rbIncome.isChecked = true
            } else {
                binding.rbExpense.isChecked = true
            }

            selectedDate.time = txn.date.toDate()
            updateDateEditText()
        }
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null || transactionId == null) return

        val description = binding.etDescription.text.toString()
        val amountStr = binding.etAmount.text.toString()
        val category = binding.etCategory.text.toString()
        val type = if (binding.rbIncome.isChecked) "income" else "expense"
        val amount = amountStr.toDoubleOrNull()
        val date = Timestamp(selectedDate.time)

        if (description.isBlank() || category.isBlank() || amount == null || amount <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "type" to type,
            "description" to description,
            "amount" to amount,
            "category" to category.replaceFirstChar { it.uppercase() },
            "date" to date
        )

        binding.btnSaveChanges.isEnabled = false
        binding.btnSaveChanges.text = "Saving..."

        db.collection("users/$userId/transactions")
            .document(transactionId!!)
            .update(updatedData)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnSaveChanges.isEnabled = true
                binding.btnSaveChanges.text = "Save Changes"
                finish()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
