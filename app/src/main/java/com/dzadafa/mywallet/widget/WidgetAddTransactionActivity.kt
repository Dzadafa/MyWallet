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
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class WidgetAddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetAddTransactionBinding

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

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show()
            finish() 
            return
        }

        val newTransaction = Transaction(
            type = type,
            description = description,
            amount = amount,
            category = category.replaceFirstChar { it.uppercase() },
            date = Timestamp.now()
        )

        binding.btnWidgetSaveTransaction.isEnabled = false
        binding.btnWidgetSaveTransaction.text = "Saving..."

        Firebase.firestore.collection("users/$userId/transactions")
            .add(newTransaction)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnWidgetSaveTransaction.isEnabled = true
                binding.btnWidgetSaveTransaction.text = getString(R.string.add_transaction)
                finish()
            }
    }
}
