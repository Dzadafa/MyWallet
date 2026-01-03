package com.dzadafa.mywallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.databinding.ItemBudgetBinding
import com.dzadafa.mywallet.utils.Utils

class BudgetAdapter(
    private val onEditClick: (Budget) -> Unit,
    private val onDeleteClick: (Budget) -> Unit
) : ListAdapter<Budget, BudgetAdapter.BudgetViewHolder>(BudgetDiffCallback) {

    inner class BudgetViewHolder(private val binding: ItemBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: Budget) {
            binding.tvCategoryName.text = budget.category
            binding.tvLimitAmount.text = "Limit: ${Utils.formatAsRupiah(budget.limitAmount)}"
            
            binding.btnEdit.setOnClickListener { onEditClick(budget) }
            binding.btnDelete.setOnClickListener { onDeleteClick(budget) }
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

object BudgetDiffCallback : DiffUtil.ItemCallback<Budget>() {
    override fun areItemsTheSame(oldItem: Budget, newItem: Budget) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Budget, newItem: Budget) = oldItem == newItem
}
