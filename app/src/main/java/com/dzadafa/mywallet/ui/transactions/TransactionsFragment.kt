package com.dzadafa.mywallet.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.FilterManager
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.adapter.TransactionAdapter
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.databinding.DialogAddTransactionBinding
import com.dzadafa.mywallet.databinding.FragmentTransactionsBinding
import com.dzadafa.mywallet.ui.edit.EditTransactionActivity
import java.util.Calendar
import java.util.Date

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private var budgetCategories: List<String> = emptyList()

    private val incomeCategories = listOf("Salary", "Allowance", "Bonus", "Investment", "Gift", "Other")

    private val viewModel: TransactionsViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication).budgetRepository,
            (requireActivity().application as MyWalletApplication).investmentRepository,
            (requireActivity().application as MyWalletApplication)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerViews()
        setupObservers()
        setupListeners()
        updateFilterButtonText()

        return root
    }

    private fun setupListeners() {
        binding.fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun updateFilterButtonText() {
        binding.btnFilter.text = FilterManager.getFilterDisplayString(requireContext())
    }

    private fun showFilterDialog() {
        val filters = FilterManager.FilterType.entries.toTypedArray()
        val filterNames = filters.map { type ->
            when (type) {
                FilterManager.FilterType.THIS_MONTH -> getString(R.string.this_month)
                FilterManager.FilterType.LAST_3_MONTHS -> getString(R.string.last_3_months)
                FilterManager.FilterType.THIS_YEAR -> getString(R.string.this_year)
                FilterManager.FilterType.ALL_TIME -> getString(R.string.all_time)
            }
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_filter))
            .setItems(filterNames) { _, which ->
                val selectedFilter = filters[which]

                val cal = Calendar.getInstance()
                FilterManager.saveFilterState(
                    requireContext(), 
                    selectedFilter, 
                    cal.get(Calendar.YEAR), 
                    cal.get(Calendar.MONTH)
                )
                updateFilterButtonText()
                viewModel.forceFilterUpdate()
            }
            .show()
    }

    private fun setupRecyclerViews() {
        val incomeAdapter = TransactionAdapter { transaction ->
            openEditTransaction(transaction)
        }
        binding.rvIncome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incomeAdapter
        }

        val expenseAdapter = TransactionAdapter { transaction ->
            openEditTransaction(transaction)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun openEditTransaction(transaction: Transaction) {
        val intent = Intent(requireContext(), EditTransactionActivity::class.java).apply {
            putExtra("TRANSACTION_ID", transaction.id)
        }
        startActivity(intent)
    }

    private fun setupObservers() {
        viewModel.incomeList.observe(viewLifecycleOwner) { transactions ->
            (binding.rvIncome.adapter as TransactionAdapter).submitList(transactions)
            binding.tvNoIncome.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.expenseList.observe(viewLifecycleOwner) { transactions ->
            (binding.rvExpenses.adapter as TransactionAdapter).submitList(transactions)
            binding.tvNoExpenses.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.allBudgets.observe(viewLifecycleOwner) { budgets ->
            budgetCategories = budgets.map { it.category }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->

        }
    }

    private fun showAddTransactionDialog() {
        val dialogBinding = DialogAddTransactionBinding.inflate(layoutInflater)

        fun updateCategoryAdapter(isExpense: Boolean) {
            val list = if (isExpense) budgetCategories else incomeCategories
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, list)
            dialogBinding.etCategory.setAdapter(adapter)
        }

        updateCategoryAdapter(dialogBinding.rbExpense.isChecked)

        dialogBinding.rgType.setOnCheckedChangeListener { _, checkedId ->
            dialogBinding.etCategory.text = null 

            updateCategoryAdapter(checkedId == R.id.rb_expense)
        }

        dialogBinding.etCategory.setOnClickListener { dialogBinding.etCategory.showDropDown() }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Transaction")
            .setView(dialogBinding.root)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val description = dialogBinding.etDescription.text.toString()
                val amount = dialogBinding.etAmount.text.toString()
                val category = dialogBinding.etCategory.text.toString()
                val type = if (dialogBinding.rbIncome.isChecked) "income" else "expense"
                val date = Date()

                viewModel.addTransaction(type, description, amount, category, date)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
