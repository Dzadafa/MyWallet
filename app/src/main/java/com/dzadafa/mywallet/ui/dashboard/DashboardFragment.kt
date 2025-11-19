package com.dzadafa.mywallet.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.databinding.FragmentDashboardBinding
import com.dzadafa.mywallet.utils.Utils
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupChartStyles()
        setupObservers()

        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateDashboardData()
    }

    private fun setupObservers() {
        viewModel.currentBalance.observe(viewLifecycleOwner) { balance ->
            binding.tvCurrentBalance.text = Utils.formatAsRupiah(balance)
            val color = if (balance < 0) R.color.expense_red else R.color.text_default_color
            binding.tvCurrentBalance.setTextColor(ContextCompat.getColor(requireContext(), color))
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = Utils.formatAsRupiah(income)
            updateBarChart(income, viewModel.totalExpense.value ?: 0.0)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = Utils.formatAsRupiah(expense)
            updateBarChart(viewModel.totalIncome.value ?: 0.0, expense)
        }

        viewModel.expenseBreakdown.observe(viewLifecycleOwner) { breakdown ->
            updatePieChart(breakdown)
        }
    }

    private fun setupChartStyles() {
        val noDataText = getString(R.string.no_data_to_display)

        binding.pieChart.apply {
            description.isEnabled = false
            setDrawEntryLabels(false)
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.widget_text_color)
            legend.isWordWrapEnabled = true
            setUsePercentValues(true)
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            setNoDataText(noDataText)
            animateY(1000, Easing.EaseInOutCubic)
        }

        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.widget_text_color)
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            setNoDataText(noDataText)
            animateY(1000, Easing.EaseInOutCubic)
        }
    }

    private fun updatePieChart(breakdown: Map<String, Double>) {
        if (breakdown.isEmpty()) {
            binding.pieChart.clear()
            binding.pieChart.invalidate()
            return
        }

        val entries = breakdown.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Expense Breakdown")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.VORDIPLOM_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.widget_text_color)
        dataSet.sliceSpace = 2f

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(binding.pieChart))
        
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
        binding.pieChart.animateY(1000, Easing.EaseInOutCubic)
    }

    private fun updateBarChart(income: Double, expense: Double) {
        if (income == 0.0 && expense == 0.0) {
            binding.barChart.clear()
            binding.barChart.invalidate()
            return
        }

        val incomeEntry = BarEntry(0f, income.toFloat())
        val expenseEntry = BarEntry(1f, expense.toFloat())
        
        val incomeDataSet = BarDataSet(listOf(incomeEntry), "Income")
        incomeDataSet.color = ContextCompat.getColor(requireContext(), R.color.income_green)
        incomeDataSet.valueTextSize = 12f

        val expenseDataSet = BarDataSet(listOf(expenseEntry), "Expense")
        expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.expense_red)
        expenseDataSet.valueTextSize = 12f

        val barData = BarData(incomeDataSet, expenseDataSet)
        barData.barWidth = 0.4f
        
        binding.barChart.data = barData
        binding.barChart.groupBars(0f, 0.2f, 0f)
        binding.barChart.invalidate()
        binding.barChart.animateY(1000, Easing.EaseInOutCubic)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
