package com.dzadafa.mywallet.ui.investment

import android.graphics.Color
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
import com.dzadafa.mywallet.adapter.InvestmentAdapter
import com.dzadafa.mywallet.databinding.DialogAddInvestmentBinding
import com.dzadafa.mywallet.databinding.FragmentInvestmentBinding
import com.dzadafa.mywallet.utils.Utils

class InvestmentFragment : Fragment() {

    private var _binding: FragmentInvestmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InvestmentViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication).budgetRepository,
            (requireActivity().application as MyWalletApplication).investmentRepository, 

            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupHeader()

        binding.fabAddInvestment.setOnClickListener {
            showAddDialog()
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        val adapter = InvestmentAdapter { investment ->
             val intent = android.content.Intent(requireContext(), InvestmentDetailActivity::class.java).apply {
                 putExtra("INVESTMENT_ID", investment.id)
             }
             startActivity(intent)
        }
        binding.rvInvestments.layoutManager = LinearLayoutManager(context)
        binding.rvInvestments.adapter = adapter

        viewModel.allInvestments.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    private fun setupHeader() {
        viewModel.totalPortfolioValue.observe(viewLifecycleOwner) { value ->
            binding.tvTotalValue.text = Utils.formatAsRupiah(value)
        }

        viewModel.totalProfitLoss.observe(viewLifecycleOwner) { (amount, percent) ->
            val amountStr = Utils.formatAsRupiah(amount)
            val percentStr = String.format("%.2f%%", percent)
            val sign = if (amount >= 0) "+" else ""
            binding.tvTotalPl.text = "$sign$amountStr ($sign$percentStr)"

            val color = if (amount >= 0) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
            binding.tvTotalPl.setTextColor(color)
        }
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddInvestmentBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Asset")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etName.text.toString()
                val type = dialogBinding.etType.text.toString()
                val priceStr = dialogBinding.etCurrentPrice.text.toString()
                val dcaStr = dialogBinding.etDcaTarget.text.toString()

                val price = priceStr.toDoubleOrNull() ?: 0.0
                val dca = dcaStr.toDoubleOrNull() ?: 0.0

                viewModel.addInvestment(name, type, price, dca)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
