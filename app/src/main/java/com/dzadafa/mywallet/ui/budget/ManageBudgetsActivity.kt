package com.dzadafa.mywallet.ui.budget

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.adapter.BudgetAdapter
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.databinding.ActivityManageBudgetsBinding
import com.dzadafa.mywallet.databinding.DialogAddBudgetBinding

class ManageBudgetsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageBudgetsBinding
    private val viewModel: BudgetViewModel by viewModels {
        MyWalletViewModelFactory(
            (application as MyWalletApplication).transactionRepository,
            (application as MyWalletApplication).wishlistRepository,
            (application as MyWalletApplication).budgetRepository,
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBudgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Budgets"

        setupRecyclerView()
        
        binding.fabAddBudget.setOnClickListener {
            showAddEditDialog(null)
        }

        viewModel.toastMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val adapter = BudgetAdapter(
            onEditClick = { budget -> showAddEditDialog(budget) },
            onDeleteClick = { budget -> showDeleteDialog(budget) }
        )
        binding.rvBudgets.layoutManager = LinearLayoutManager(this)
        binding.rvBudgets.adapter = adapter

        viewModel.allBudgets.observe(this) { list ->
            adapter.submitList(list)
        }
    }

    private fun showAddEditDialog(budget: Budget?) {
        val dialogBinding = DialogAddBudgetBinding.inflate(layoutInflater)
        
        if (budget != null) {
            dialogBinding.etCategory.setText(budget.category)
            dialogBinding.etCategory.isEnabled = false 
            dialogBinding.etLimit.setText(budget.limitAmount.toInt().toString())
        }

        val title = if (budget == null) "New Category" else "Edit Limit"

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val category = dialogBinding.etCategory.text.toString()
                val limitStr = dialogBinding.etLimit.text.toString()
                val limit = limitStr.toDoubleOrNull()

                if (category.isNotBlank() && limit != null && limit >= 0) {
                    if (budget == null) {
                        viewModel.insert(category, limit)
                    } else {
                        viewModel.update(budget.copy(limitAmount = limit))
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please check your input", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun showDeleteDialog(budget: Budget) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Delete '${budget.category}'? This will not delete transactions, but the category won't be selectable anymore.")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(budget) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
