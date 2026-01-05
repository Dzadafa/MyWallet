package com.dzadafa.mywallet.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.adapter.BudgetAdapter
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.databinding.DialogAddBudgetBinding
import com.dzadafa.mywallet.databinding.FragmentBudgetBinding

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication).budgetRepository,
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.fabAddBudget.setOnClickListener {
            showAddEditDialog(null)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val adapter = BudgetAdapter { budget -> 
            showAddEditDialog(budget) 
        }

        binding.rvBudgets.layoutManager = LinearLayoutManager(context)
        binding.rvBudgets.adapter = adapter

        viewModel.budgetList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmptyBudget.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showAddEditDialog(budget: Budget?) {
        val dialogBinding = DialogAddBudgetBinding.inflate(layoutInflater)

        if (budget != null) {
            dialogBinding.etCategory.setText(budget.category)
            dialogBinding.etCategory.isEnabled = false 

            dialogBinding.etLimit.setText(budget.limitAmount.toInt().toString())
        }

        val title = if (budget == null) "New Budget Category" else "Edit Budget Limit"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .setNeutralButton(if (budget != null) "Delete" else null) { _, _ ->
                if (budget != null) showDeleteDialog(budget)
            }
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
                    Toast.makeText(context, "Please check your input", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun showDeleteDialog(budget: Budget) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Delete '${budget.category}'? This will remove the budget tracking for this category.")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(budget) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
