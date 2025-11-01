package com.dzadafa.mywallet.utils

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

    /**
     * Formats a Double into Indonesian Rupiah (Rp)
     * e.g., 10000.0 -> "Rp 10.000"
     */
    fun formatAsRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(amount)
    }

    /**
     * Formats a Firebase Timestamp into a simple date
     * e.g., "24 Oct 2025"
     */
    fun formatTimestamp(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }
}
