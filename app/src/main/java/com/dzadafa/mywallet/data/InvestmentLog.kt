package com.dzadafa.mywallet.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "investment_logs",
    foreignKeys = [
        ForeignKey(
            entity = Investment::class,
            parentColumns = ["id"],
            childColumns = ["investmentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("investmentId")]
)
data class InvestmentLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val investmentId: Int,
    val date: Date,
    val type: String, 

    val amountInvested: Double, 

    val units: Double, 

    val pricePerUnit: Double 

)
