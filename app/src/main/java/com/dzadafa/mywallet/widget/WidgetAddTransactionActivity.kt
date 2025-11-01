package com.dzadafa.mywallet.widget

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.databinding.ActivityWidgetAddTransactionBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
// --- THIS IS THE NEW, CORRECT IMPORT ---
import com.google.firebase.firestore.firestore
// --- THIS IS THE NEW, CORRECT IMPORT ---
import com.google.firebase.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// --- THIS IS THE IMPORT FOR .await() ---
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class WidgetAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the title for clarity
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

        // --- Validation ---
        if (description.isBlank() || category.isBlank() || amount == null || amount <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close activity if not logged in
            return
        }

        val newTransaction = Transaction(
            type = type,
            description = description,
            amount = amount,
            category = category.replaceFirstChar { it.uppercase() },
            date = Timestamp.now()
        )

        // Save to Firebase in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // --- THIS LINE IS NOW CORRECT ---
                Firebase.firestore.collection("users/$userId/transactions")
                    .add(newTransaction)
                    .await() // .await() comes from kotlinx-coroutines-play-services
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@WidgetAddTransactionActivity,
                        "Transaction added!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Close the activity on success
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@WidgetAddTransactionActivity,
                        "Failed to add transaction: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
