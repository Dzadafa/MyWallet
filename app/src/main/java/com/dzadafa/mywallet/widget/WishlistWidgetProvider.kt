package com.dzadafa.mywallet.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dzadafa.mywallet.MainActivity
import com.dzadafa.mywallet.R

class WishlistWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val prefs = context.getSharedPreferences("MyWalletPrefs", Context.MODE_PRIVATE)
            val itemText = prefs.getString("WISHLIST_ITEM", "No items yet")
            val messageText = prefs.getString("WISHLIST_MESSAGE", "Add items in the app!")

            val views = RemoteViews(context.packageName, R.layout.widget_wishlist).apply {
                setTextViewText(R.id.tv_widget_wishlist_item, itemText)
                setTextViewText(R.id.tv_widget_wishlist_message, messageText)
            }

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 1, // Use a different request code from the Stats widget
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root_wishlist, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
