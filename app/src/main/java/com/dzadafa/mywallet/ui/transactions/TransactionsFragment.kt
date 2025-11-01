package com.dzadafa.mywallet.ui.transactions

import android.app.DatePickerDialog
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.adapter.TransactionAdapter
import com.dzadafa.mywallet.databinding.FragmentTransactionsBinding

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var incomeAdapter: TransactionAdapter
    private lateinit var expenseAdapter: TransactionAdapter
    private val selectedDate = Calendar.getInstance()
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerViews()
        setupObservers()

        binding.btnAddTransaction.setOnClickListener {
            addTransaction()
        }

        setupDatePicker()

        return root
    }

    private fun setupDatePicker() {
        updateDateEditText()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateEditText()
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
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

    private fun setupRecyclerViews() {
        incomeAdapter = TransactionAdapter { transaction ->
            viewModel.deleteTransaction(transaction)
        }
        binding.rvIncome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = incomeAdapter
        }

        expenseAdapter = TransactionAdapter { transaction ->
            viewModel.deleteTransaction(transaction)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun setupObservers() {
        viewModel.incomeList.observe(viewLifecycleOwner) { incomeList ->
            incomeAdapter.submitList(incomeList)
        }

        viewModel.expenseList.observe(viewLifecycleOwner) { expenseList ->
            expenseAdapter.submitList(expenseList)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTransaction() {
        val description = binding.etDescription.text.toString()
        val amount = binding.etAmount.text.toString()
        val category = binding.etCategory.text.toString()

        val selectedTypeId = binding.rgType.checkedRadioButtonId
        val type = if (selectedTypeId == R.id.rb_income) "income" else "expense"
        val date = Timestamp(selectedDate.time)

        viewModel.addTransaction(type, description, amount, category, date)

        clearForm()
    }

    private fun clearForm() {
        binding.etDescription.text?.clear()
        binding.etAmount.text?.clear()
        binding.etCategory.text?.clear()
        binding.rbExpense.isChecked = true
        selectedDate.timeInMillis = System.currentTimeMillis()
        updateDateEditText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
