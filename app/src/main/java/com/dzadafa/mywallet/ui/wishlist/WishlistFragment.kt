package com.dzadafa.mywallet.ui.wishlist

import android.content.Intent
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
import com.dzadafa.mywallet.adapter.WishListAdapter
import com.dzadafa.mywallet.data.WishlistItem
import com.dzadafa.mywallet.databinding.DialogAddWishlistBinding
import com.dzadafa.mywallet.databinding.FragmentWishlistBinding
import com.dzadafa.mywallet.ui.edit.EditWishlistActivity

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WishlistViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication).budgetRepository,
            (requireActivity().application as MyWalletApplication)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupObservers()

        binding.fabAddWishlist.setOnClickListener {
            showAddWishlistDialog()
        }

        return root
    }

    private fun setupRecyclerView() {
        val adapter = WishListAdapter(
            onToggleCompleted = { item ->
                viewModel.toggleItemCompleted(item)
            },
            onEditClicked = { id ->
                val intent = Intent(requireContext(), EditWishlistActivity::class.java).apply {
                    putExtra("WISHLIST_ID", id)
                }
                startActivity(intent)
            },
            onDeleteClicked = { item ->
                showDeleteConfirmation(item)
            }
        )
        binding.rvWishlist.layoutManager = LinearLayoutManager(context)
        binding.rvWishlist.adapter = adapter
    }

    private fun setupObservers() {
        
        viewModel.analyzedWishlist.observe(viewLifecycleOwner) { items ->
            (binding.rvWishlist.adapter as WishListAdapter).submitList(items)
        }
    }

    private fun showDeleteConfirmation(item: WishlistItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${item.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteWishlistItem(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddWishlistDialog() {
        val dialogBinding = DialogAddWishlistBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("New Wishlist Item")
            .setView(dialogBinding.root)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val name = dialogBinding.etName.text.toString()
                val priceStr = dialogBinding.etPrice.text.toString()

                if (name.isNotBlank() && priceStr.isNotBlank()) {
                    
                    viewModel.addWishlistItem(name, priceStr)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
