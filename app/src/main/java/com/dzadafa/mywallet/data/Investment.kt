package com.dzadafa.mywallet.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investments")
data class Investment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,       

    val type: String,       

    val amountHeld: Double, 

    val averageBuyPrice: Double, 

    val currentPrice: Double, 

    val targetMonthlyDca: Double = 0.0 

) {

    fun getCurrentValue(): Double = amountHeld * currentPrice

    fun getTotalCost(): Double = amountHeld * averageBuyPrice

    fun getProfitLossPercentage(): Double {
        if (averageBuyPrice == 0.0) return 0.0
        return ((currentPrice - averageBuyPrice) / averageBuyPrice) * 100
    }
}
