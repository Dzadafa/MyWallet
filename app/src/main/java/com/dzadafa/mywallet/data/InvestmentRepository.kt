package com.dzadafa.mywallet.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class InvestmentRepository(private val dao: InvestmentDao) {

    val allInvestments: Flow<List<Investment>> = dao.getAllInvestments()

    suspend fun insertInvestment(investment: Investment) = dao.insertInvestment(investment)
    suspend fun updateInvestment(investment: Investment) = dao.updateInvestment(investment)
    suspend fun deleteInvestment(investment: Investment) = dao.deleteInvestment(investment)
    suspend fun getInvestmentById(id: Int) = dao.getInvestmentById(id)

    fun getLogsForInvestment(id: Int): Flow<List<InvestmentLog>> = dao.getLogsForInvestment(id)
    
    suspend fun insertLog(log: InvestmentLog) = dao.insertLog(log)
    suspend fun deleteLog(log: InvestmentLog) = dao.deleteLog(log)
    suspend fun getLogsForInvestmentSync(id: Int): List<InvestmentLog> {
        return dao.getLogsForInvestmentSync(id)
    }

    fun getMonthlyDcaProgress(investmentId: Int, startOfMonth: Date, endOfMonth: Date): Flow<Double?> {
        return dao.getTotalBoughtInRange(investmentId, startOfMonth, endOfMonth)
    }
}
