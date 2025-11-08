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

    companion object {
        const val TRANSACTION_CHANNEL_ID = "TRANSACTION_CHANNEL"
        const val REMINDER_CHANNEL_ID = "REMINDER_CHANNEL"
    }

    private val database by lazy { AppDatabase.getDatabase(this) }

    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val wishlistRepository by lazy { WishlistRepository(database.wishlistDao()) }

    override fun onCreate() {
        super.onCreate()
        ThemeManager.applyTheme(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val transactionChannel = NotificationChannel(
                TRANSACTION_CHANNEL_ID,
                "Transactions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for new transactions"
            }

            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to log transactions"
            }
            
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(transactionChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }
}
