package com.dzadafa.mywallet.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dzadafa.mywallet.MyWalletApplication
import com.dzadafa.mywallet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistToggleReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_TOGGLE_ITEM = "com.dzadafa.mywallet.ACTION_TOGGLE_ITEM"
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE_ITEM) {
            val itemId = intent.getIntExtra(EXTRA_ITEM_ID, -1)
            if (itemId == -1) return

            val repository = (context.applicationContext as MyWalletApplication).wishlistRepository
            
            CoroutineScope(Dispatchers.IO).launch {
                val item = repository.allWishlistItems.first().find { it.id == itemId }
                if (item != null) {
                    val updatedItem = item.copy(completed = !item.completed)
                    repository.update(updatedItem)

                    notifyWidgetDataChanged(context)
                }
            }
        }
    }

    private fun notifyWidgetDataChanged(context: Context) {
        val intent = Intent(context, WishlistWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(context.packageName.let { ComponentName(it, WishlistWidgetProvider::class.java.name) })
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        context.sendBroadcast(intent)
    }
}
