package com.dzadafa.mywallet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface InvestmentDao {

    @Query("SELECT * FROM investments ORDER BY name ASC")
    fun getAllInvestments(): Flow<List<Investment>>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getInvestmentById(id: Int): Investment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: Investment)

    @Update
    suspend fun updateInvestment(investment: Investment)

    @Delete
    suspend fun deleteInvestment(investment: Investment)

    @Query("SELECT * FROM investment_logs WHERE investmentId = :investmentId ORDER BY date DESC")
    fun getLogsForInvestment(investmentId: Int): Flow<List<InvestmentLog>>

    @Query("SELECT * FROM investment_logs WHERE investmentId = :investmentId ORDER BY date ASC")
    suspend fun getLogsForInvestmentSync(investmentId: Int): List<InvestmentLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InvestmentLog)

    @Delete
    suspend fun deleteLog(log: InvestmentLog)

    @Query("""
        SELECT SUM(amountInvested) FROM investment_logs 
        WHERE investmentId = :investmentId 
        AND type = 'BUY' 
        AND date BETWEEN :startDate AND :endDate
    """)
    fun getTotalBoughtInRange(investmentId: Int, startDate: Date, endDate: Date): Flow<Double?>
}
