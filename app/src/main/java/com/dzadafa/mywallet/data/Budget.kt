package com.dzadafa.mywallet.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets", 
    indices = [Index(value = ["category", "year", "month"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val year: Int,
    val month: Int 

)
