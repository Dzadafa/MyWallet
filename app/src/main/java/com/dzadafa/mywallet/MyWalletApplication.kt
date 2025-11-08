package com.dzadafa.mywallet

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Transactions"
            val descriptionText = "Notifications for new transactions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TRANSACTION_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
