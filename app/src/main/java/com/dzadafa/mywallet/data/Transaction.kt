package com.dzadafa.mywallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String = "expense",
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "Other",
    val date: Date = Date()
)
