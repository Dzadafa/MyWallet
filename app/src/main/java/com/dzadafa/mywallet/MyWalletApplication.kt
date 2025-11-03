package com.dzadafa.mywallet

import android.app.Application
import com.dzadafa.mywallet.data.AppDatabase
import com.dzadafa.mywallet.data.TransactionRepository
import com.dzadafa.mywallet.data.WishlistRepository

class MyWalletApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }

    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val wishlistRepository by lazy { WishlistRepository(database.wishlistDao()) }

    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
    }
}
