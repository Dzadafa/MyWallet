package com.dzadafa.mywallet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dzadafa.mywallet.data.BudgetRepository
import com.dzadafa.mywallet.data.InvestmentRepository
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.data.WishlistRepository
import com.dzadafa.mywallet.ui.budget.BudgetViewModel
import com.dzadafa.mywallet.ui.dashboard.DashboardViewModel
import com.dzadafa.mywallet.ui.investment.InvestmentViewModel
import com.dzadafa.mywallet.ui.transactions.TransactionsViewModel
import com.dzadafa.mywallet.ui.wishlist.WishlistViewModel

class MyWalletViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val wishlistRepository: WishlistRepository,
    private val budgetRepository: BudgetRepository,
    private val investmentRepository: InvestmentRepository, 
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(transactionRepository, budgetRepository, application) as T
        }
        if (modelClass.isAssignableFrom(WishlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WishlistViewModel(transactionRepository, wishlistRepository, application) as T
        }
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(transactionRepository, application) as T
        }
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetRepository, transactionRepository, application) as T
        }
        if (modelClass.isAssignableFrom(InvestmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvestmentViewModel(investmentRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
