package com.dzadafa.mywallet.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Transaction(
    @DocumentId val id: String? = null,
    val type: String = "expense", // "expense" or "income"
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "Other",
    val date: Timestamp = Timestamp.now()
)
