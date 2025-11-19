package com.dzadafa.mywallet

import android.content.Context
import com.dzadafa.mywallet.data.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FilterManager {

    private const val PREFS_NAME = "GlobalFilterPrefs"
    private const val KEY_FILTER_TYPE = "filter_type"
    private const val KEY_FILTER_YEAR = "filter_year"
    private const val KEY_FILTER_MONTH = "filter_month"

    enum class FilterType { THIS_MONTH, LAST_3_MONTHS, THIS_YEAR, ALL_TIME }

    private val displayMonthFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    private fun getDefaultFilterType(): FilterType {
        return FilterType.THIS_MONTH
    }

    fun saveFilterState(context: Context, type: FilterType, year: Int, month: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putInt(KEY_FILTER_TYPE, type.ordinal)
            putInt(KEY_FILTER_YEAR, year)
            putInt(KEY_FILTER_MONTH, month)
            apply()
        }
    }

    fun getFilterState(context: Context): Triple<FilterType, Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val typeOrdinal = prefs.getInt(KEY_FILTER_TYPE, getDefaultFilterType().ordinal)
        val type = FilterType.entries.getOrElse(typeOrdinal) { getDefaultFilterType() }
        
        val currentCal = Calendar.getInstance()
        val year = prefs.getInt(KEY_FILTER_YEAR, currentCal.get(Calendar.YEAR))
        val month = prefs.getInt(KEY_FILTER_MONTH, currentCal.get(Calendar.MONTH)) 

        return Triple(type, year, month)
    }

    fun getFilterDisplayString(context: Context): String {
        val (type, year, month) = getFilterState(context)
        return when (type) {
            FilterType.THIS_MONTH -> context.getString(R.string.this_month)
            FilterType.LAST_3_MONTHS -> context.getString(R.string.last_3_months)
            FilterType.THIS_YEAR -> context.getString(R.string.this_year)
            FilterType.ALL_TIME -> context.getString(R.string.all_time)
            else -> {
                val cal = Calendar.getInstance().apply { set(year, month, 1) }
                displayMonthFormat.format(cal.time)
            }
        }
    }

    fun getFilterStartDate(context: Context): Date {
        val (type, year, month) = getFilterState(context)
        val cal = Calendar.getInstance()

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return when (type) {
            FilterType.THIS_MONTH -> {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.time
            }
            FilterType.LAST_3_MONTHS -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.MONTH, -2)
                cal.time
            }
            FilterType.THIS_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.time
            }
            FilterType.ALL_TIME -> Date(0)
        }
    }
    
    fun filterTransactions(context: Context, transactions: List<Transaction>): List<Transaction> {
        val startTime = getFilterStartDate(context)
        if (getFilterState(context).first == FilterType.ALL_TIME) {
            return transactions
        }
        return transactions.filter {
            it.date.after(startTime) || it.date == startTime
        }
    }
}
