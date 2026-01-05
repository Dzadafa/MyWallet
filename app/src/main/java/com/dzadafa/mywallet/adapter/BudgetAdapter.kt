package com.dzadafa.mywallet.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.databinding.ItemBudgetBinding
import com.dzadafa.mywallet.ui.budget.BudgetWithUsage
import com.dzadafa.mywallet.utils.Utils

class BudgetAdapter(
    private val onEditClick: (Budget) -> Unit
) : ListAdapter<BudgetWithUsage, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback) {

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetWithUsage) {
            val budget = item.budget
            binding.tvCategoryName.text = budget.category
            binding.tvLimitAmount.text = "Limit: ${Utils.formatAsRupiah(budget.limitAmount)}"
            binding.tvSpentAmount.text = "Spent: ${Utils.formatAsRupiah(item.spent)}"
            binding.tvPercent.text = "${item.progressPercent}%"

            binding.pbBudget.progress = item.progressPercent

            val color = if (item.isOverBudget) Color.RED else Color.parseColor("#4CAF50") 

            binding.pbBudget.progressTintList = ColorStateList.valueOf(color)
            binding.tvPercent.setTextColor(color)

            binding.btnEdit.setOnClickListener { onEditClick(budget) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object BudgetDiffCallback : DiffUtil.ItemCallback<BudgetWithUsage>() {
    override fun areItemsTheSame(oldItem: BudgetWithUsage, newItem: BudgetWithUsage) = oldItem.budget.id == newItem.budget.id
    override fun areContentsTheSame(oldItem: BudgetWithUsage, newItem: BudgetWithUsage) = oldItem == newItem
}
