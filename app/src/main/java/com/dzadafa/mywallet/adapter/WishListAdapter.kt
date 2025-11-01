package com.dzadafa.mywallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.WishlistItem
import com.dzadafa.mywallet.databinding.ItemWishlistBinding
import com.dzadafa.mywallet.utils.Utils

data class WishlistItemAnalysis(
    val item: WishlistItem,
    val affordabilityMessage: String,
    val canAfford: Boolean,
    val isBudgetNegative: Boolean
)

class WishlistAdapter(
    private val onDeleteClicked: (WishlistItem) -> Unit
) : ListAdapter<WishlistItemAnalysis, WishlistAdapter.WishlistViewHolder>(WishlistDiffCallback) {

    inner class WishlistViewHolder(private val binding: ItemWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(analysis: WishlistItemAnalysis) {
            val item = analysis.item
            binding.tvItemName.text = item.name
            binding.tvItemPrice.text = Utils.formatAsRupiah(item.price)

            binding.tvAffordability.text = analysis.affordabilityMessage
            
            val colorRes = when {
                analysis.canAfford -> R.color.income_green
                analysis.isBudgetNegative -> R.color.expense_red
                else -> R.color.text_default_color
            }
            binding.tvAffordability.setTextColor(ContextCompat.getColor(itemView.context, colorRes))

            binding.ivDelete.setOnClickListener {
                onDeleteClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding = ItemWishlistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object WishlistDiffCallback : DiffUtil.ItemCallback<WishlistItemAnalysis>() {
    override fun areItemsTheSame(oldItem: WishlistItemAnalysis, newItem: WishlistItemAnalysis): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: WishlistItemAnalysis, newItem: WishlistItemAnalysis): Boolean {
        return oldItem == newItem
    }
}
