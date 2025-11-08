package com.dzadafa.mywallet.ui.wishlist

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.adapter.WishlistItemAnalysis
import com.dzadafa.mywallet.data.Transaction
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.data.WishlistItem
import com.dzadafa.mywallet.data.WishlistRepository
import com.dzadafa.mywallet.utils.Utils
import com.dzadafa.mywallet.widget.WishlistWidgetProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.math.ceil

class WishlistViewModel(
    private val transactionRepository: TransactionRepository,
    private val wishlistRepository: WishlistRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _analyzedWishlist = MutableLiveData<List<WishlistItemAnalysis>>()
    val analyzedWishlist: LiveData<List<WishlistItemAnalysis>> = _analyzedWishlist

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private val allTransactions: LiveData<List<Transaction>> = transactionRepository.allTransactions.asLiveData()
    private val allWishlistItems: LiveData<List<WishlistItem>> = wishlistRepository.allWishlistItems.asLiveData()

    init {
        allTransactions.observeForever { runAnalysis() }
        allWishlistItems.observeForever { runAnalysis() }
    }

    private fun runAnalysis() {
        val transactions = allTransactions.value ?: emptyList()
        val wishlistItems = allWishlistItems.value ?: emptyList()

        if (wishlistItems.isEmpty()) {
            _analyzedWishlist.value = emptyList()
            saveDataToPreferences(null)
            return
        }

        val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val currentBalance = totalIncome - totalExpense
        val (avgMonthlySavings, isBudgetNegative) = calculateAverageMonthlySavings(transactions)

        val analysisList = wishlistItems.map { item ->
            val canAfford = currentBalance >= item.price
            var message: String
            var budgetNegative = false

            if (item.completed) {
                message = "Purchased!"
            } else if (canAfford) {
                message = "You can afford this now!"
            } else if (isBudgetNegative) {
                message = "Your expenses match or exceed your income. Review your budget to save for this."
                budgetNegative = true
            } else if (avgMonthlySavings > 0) {
                val monthsNeeded = ceil(item.price / avgMonthlySavings).toInt()
                val monthString = if (monthsNeeded == 1) "1 month" else "$monthsNeeded months"
                message = "At your current savings rate, you can get this in about $monthString."
            } else {
                message = "You aren't saving money right now. Review your budget."
                budgetNegative = true
            }

            WishlistItemAnalysis(
                item = item,
                affordabilityMessage = message,
                canAfford = canAfford,
                isBudgetNegative = budgetNegative
            )
        }

        _analyzedWishlist.value = analysisList.sortedWith(
            compareBy<WishlistItemAnalysis> { it.item.completed }
                .thenByDescending { it.canAfford }
                .thenBy { it.item.price }
        )

        saveDataToPreferences(analysisList)
    }

    private fun saveDataToPreferences(analysisList: List<WishlistItemAnalysis>?) {
        val prefs = getApplication<Application>().getSharedPreferences(
            "MyWalletPrefs",
            Context.MODE_PRIVATE
        )

        var itemText = "No items yet"
        var messageText = "Add items in the app!"

        if (!analysisList.isNullOrEmpty()) {
            val relevantItem = analysisList.firstOrNull { !it.item.completed }
            
            if (relevantItem != null) {
                itemText = "${relevantItem.item.name} - ${Utils.formatAsRupiah(relevantItem.item.price)}"
                messageText = relevantItem.affordabilityMessage
            } else {
                itemText = "All goals complete!"
                messageText = "Add a new item to your wishlist."
            }
        }

        with(prefs.edit()) {
            putString("WISHLIST_ITEM", itemText)
            putString("WISHLIST_MESSAGE", messageText)
            apply()
        }
    }

    private fun calculateAverageMonthlySavings(transactions: List<Transaction>): Pair<Double, Boolean> {
        if (transactions.isEmpty()) return Pair(0.0, false)
        val monthlySummary = transactions.groupBy {
            val cal = Calendar.getInstance()
            cal.time = it.date
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}"
        }
        val currentMonthKey = "${Calendar.getInstance().get(Calendar.YEAR)}-${Calendar.getInstance().get(Calendar.MONTH)}"
        val completedMonthsSummary = if (monthlySummary.size > 1) {
            monthlySummary.filterKeys { it != currentMonthKey }
        } else {
            monthlySummary
        }
        if (completedMonthsSummary.isEmpty()) return Pair(0.0, false)
        var totalSavings = 0.0
        for (monthTransactions in completedMonthsSummary.values) {
            val income = monthTransactions.filter { it.type == "income" }.sumOf { it.amount }
            val expense = monthTransactions.filter { it.type == "expense" }.sumOf { it.amount }
            totalSavings += (income - expense)
        }
        val averageSavings = totalSavings / completedMonthsSummary.size
        return Pair(averageSavings, averageSavings <= 0)
    }

    fun addWishlistItem(name: String, priceStr: String) {
        if (name.isBlank()) {
            _toastMessage.value = "Please enter an item name"
            return
        }
        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            _toastMessage.value = "Please enter a valid price"
            return
        }

        val newItem = WishlistItem(name = name, price = price, completed = false)

        viewModelScope.launch {
            wishlistRepository.insert(newItem)
            _toastMessage.postValue("Item added to wishlist!")
            notifyWidgetDataChanged()
        }
    }

    fun toggleItemCompleted(item: WishlistItem) {
        val updatedItem = item.copy(completed = !item.completed)
        viewModelScope.launch {
            wishlistRepository.update(updatedItem)
            val toastMessage = if (updatedItem.completed) "Goal achieved!" else "Goal restored"
            _toastMessage.postValue(toastMessage)
            notifyWidgetDataChanged()
        }
    }

    fun deleteWishlistItem(item: WishlistItem) {
        viewModelScope.launch {
            wishlistRepository.deleteById(item.id)
            _toastMessage.postValue("Item deleted")
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
