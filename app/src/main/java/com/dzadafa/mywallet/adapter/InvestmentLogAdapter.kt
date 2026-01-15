package com.dzadafa.mywallet.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dzadafa.mywallet.data.InvestmentLog
import com.dzadafa.mywallet.databinding.ItemInvestmentLogBinding
import com.dzadafa.mywallet.utils.Utils

class InvestmentLogAdapter(
    private val onDeleteClick: (InvestmentLog) -> Unit
) : ListAdapter<InvestmentLog, InvestmentLogAdapter.LogViewHolder>(LogDiffCallback) {

    inner class LogViewHolder(private val binding: ItemInvestmentLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InvestmentLog) {
            binding.tvLogType.text = item.type
            binding.tvLogDate.text = Utils.formatDate(item.date)

            binding.tvLogUnits.text = "${Utils.formatDecimal(item.units)} Units"
            binding.tvLogPrice.text = "@ ${Utils.formatAsRupiah(item.pricePerUnit)}"

            if (item.type == "BUY") {
                binding.tvLogType.setTextColor(Color.parseColor("#4CAF50")) 

            } else {
                binding.tvLogType.setTextColor(Color.parseColor("#F44336")) 

            }

            binding.btnDeleteLog.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemInvestmentLogBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object LogDiffCallback : DiffUtil.ItemCallback<InvestmentLog>() {
    override fun areItemsTheSame(oldItem: InvestmentLog, newItem: InvestmentLog) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: InvestmentLog, newItem: InvestmentLog) = oldItem == newItem
}
