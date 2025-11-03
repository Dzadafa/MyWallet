package com.dzadafa.mywallet.ui.wishlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.MyWalletViewModelFactory
import com.dzadafa.mywallet.adapter.WishlistAdapter
import com.dzadafa.mywallet.databinding.FragmentWishlistBinding

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WishlistViewModel by viewModels {
        MyWalletViewModelFactory(
            (requireActivity().application as MyWalletApplication).transactionRepository,
            (requireActivity().application as MyWalletApplication).wishlistRepository,
            (requireActivity().application as MyWalletApplication)
        )
    }

    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupObservers()

        binding.btnAddWishlistItem.setOnClickListener {
            addWishlistItem()
        }

        return root
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistAdapter { wishlistItem ->
            viewModel.toggleItemCompleted(wishlistItem)
        }
        binding.rvWishlist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wishlistAdapter
        }
    }

    private fun setupObservers() {
        viewModel.analyzedWishlist.observe(viewLifecycleOwner) { analyzedList ->
            wishlistAdapter.submitList(analyzedList)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun addWishlistItem() {
        val name = binding.etItemName.text.toString()
        val price = binding.etItemPrice.text.toString()
        viewModel.addWishlistItem(name, price)
        clearForm()
    }

    private fun clearForm() {
        binding.etItemName.text?.clear()
        binding.etItemPrice.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
