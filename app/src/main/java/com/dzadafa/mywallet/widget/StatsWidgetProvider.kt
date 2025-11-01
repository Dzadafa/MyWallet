package com.dzadafa.mywallet.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.dzadafa.mywallet.MainActivity
import com.dzadafa.mywallet.R

class StatsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val prefs = context.getSharedPreferences("MyWalletPrefs", Context.MODE_PRIVATE)
            val balance = prefs.getString("BALANCE", "Rp 0")
            val income = prefs.getString("INCOME", "Rp 0")
            val expense = prefs.getString("EXPENSE", "Rp 0")

            val views = RemoteViews(context.packageName, R.layout.widget_stats).apply {
                setTextViewText(R.id.tv_widget_balance, balance)
                setTextViewText(R.id.tv_widget_income, income)
                setTextViewText(R.id.tv_widget_expense, expense)
            }

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
