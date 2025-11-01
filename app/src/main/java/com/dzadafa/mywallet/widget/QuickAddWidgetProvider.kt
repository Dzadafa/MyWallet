package com.dzadafa.mywallet.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dzadafa.mywallet.R

class QuickAddWidgetProvider : AppWidgetProvider() {

    // Called when the widget is first created and updated
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // No longer need onDeleted as we removed the associated prefs

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Construct the RemoteViews object for the simplified layout
            val views = RemoteViews(context.packageName, R.layout.widget_quick_add)

            // Create an Intent to launch WidgetAddTransactionActivity
            val intent = Intent(context, WidgetAddTransactionActivity::class.java).apply {
                // Add flags to launch as a new task if needed, especially from widget context
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Create the PendingIntent for the button click
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId, // Use widget ID as unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Set the click listener for the button
            views.setOnClickPendingIntent(R.id.btn_widget_open_add_screen, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
