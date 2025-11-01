package com.dzadafa.mywallet.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

    
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupFilterDropdown()
        setupChartStyles()
        setupObservers()

        return root
    }

    private fun setupFilterDropdown() {
        
        val filterOptions = resources.getStringArray(R.array.time_filter_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, filterOptions)
        binding.acFilter.setAdapter(adapter)

        
        binding.acFilter.setOnItemClickListener { _, _, position, _ ->
            val selectedFilter = when (position) {
                1 -> TimeFilter.THIS_MONTH
                2 -> TimeFilter.THIS_YEAR
                else -> TimeFilter.ALL_TIME
            }
            
            viewModel.setFilter(selectedFilter)
        }
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
            legend.isWordWrapEnabled = true
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.widget_text_color)
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
            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.text_default_color)
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
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_default_color)
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
