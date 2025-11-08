package com.dzadafa.mywallet.ui.edit

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.R
import com.dzadafa.mywallet.data.WishlistItem
import com.dzadafa.mywallet.data.WishlistRepository
import com.dzadafa.mywallet.databinding.ActivityEditWishlistBinding
import com.dzadafa.mywallet.widget.WishlistWidgetProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditWishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWishlistBinding
    private var wishlistItemId: Int = 0
    private var currentWishlistItem: WishlistItem? = null

    private val viewModel: EditWishlistViewModel by viewModels {
        EditWishlistViewModelFactory(
            (application as MyWalletApplication),
            (application as MyWalletApplication).wishlistRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarEdit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wishlistItemId = intent.getIntExtra("WISHLIST_ITEM_ID", 0)
        if (wishlistItemId == 0) {
            Toast.makeText(this, "Error: Invalid item ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.loadWishlistItem(wishlistItemId)

        viewModel.wishlistItem.observe(this) { item ->
            if (item != null) {
                currentWishlistItem = item
                populateUi(item)
            }
        }

        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }
        
        binding.btnDeleteItem.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }
    
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this wishlist item?")
            .setPositiveButton("Delete") { _, _ ->
                deleteItemAndFinish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteItemAndFinish() {
        viewModel.deleteItem(wishlistItemId)
        Toast.makeText(applicationContext, "Item deleted", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun populateUi(item: WishlistItem) {
        binding.etItemName.setText(item.name)
        binding.etItemPrice.setText(item.price.toString())
    }

    private fun saveChanges() {
        val name = binding.etItemName.text.toString()
        val priceStr = binding.etItemPrice.text.toString()
        val price = priceStr.toDoubleOrNull()
        val currentItem = currentWishlistItem ?: return

        if (name.isBlank() || price == null || price <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedItem = currentItem.copy(
            name = name,
            price = price
        )

        binding.btnSaveChanges.isEnabled = false
        binding.btnSaveChanges.text = "Saving..."

        viewModel.saveChanges(updatedItem)

        Toast.makeText(applicationContext, "Changes saved!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

class EditWishlistViewModel(
    private val repository: WishlistRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _wishlistItem = MutableLiveData<WishlistItem?>()
    val wishlistItem: LiveData<WishlistItem?> = _wishlistItem

    fun loadWishlistItem(id: Int) {
        viewModelScope.launch {
            _wishlistItem.value = repository.allWishlistItems.first().find { it.id == id }
        }
    }

    fun saveChanges(item: WishlistItem) {
        viewModelScope.launch {
            repository.update(item)
            notifyWidgetDataChanged()
        }
    }
    
    fun deleteItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            notifyWidgetDataChanged()
        }
    }
    
    private fun notifyWidgetDataChanged() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, WishlistWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(context.packageName.let { ComponentName(it, WishlistWidgetProvider::class.java.name) })
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}

class EditWishlistViewModelFactory(
    private val application: Application,
    private val repository: WishlistRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditWishlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditWishlistViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
