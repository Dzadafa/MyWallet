package com.dzadafa.mywallet.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dzadafa.mywallet.data.Investment
import com.dzadafa.mywallet.databinding.ItemInvestmentBinding
import com.dzadafa.mywallet.utils.Utils

class InvestmentAdapter(
    private val onItemClick: (Investment) -> Unit
) : ListAdapter<Investment, InvestmentAdapter.InvestmentViewHolder>(InvestmentDiffCallback) {

    inner class InvestmentViewHolder(private val binding: ItemInvestmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Investment) {
            binding.tvAssetName.text = item.name
            binding.tvAssetType.text = item.type
            binding.tvAssetValue.text = Utils.formatAsRupiah(item.getCurrentValue())

            val plPercent = item.getProfitLossPercentage()
            val plString = String.format("%.2f%%", plPercent)
            binding.tvAssetPl.text = if (plPercent >= 0) "+$plString" else plString
            
            val color = if (plPercent >= 0) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
            binding.tvAssetPl.setTextColor(color)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentViewHolder {
        val binding = ItemInvestmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InvestmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvestmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object InvestmentDiffCallback : DiffUtil.ItemCallback<Investment>() {
    override fun areItemsTheSame(oldItem: Investment, newItem: Investment) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Investment, newItem: Investment) = oldItem == newItem
}
