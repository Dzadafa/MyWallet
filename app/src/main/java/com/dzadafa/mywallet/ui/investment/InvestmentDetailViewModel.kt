package com.dzadafa.mywallet.ui.investment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.data.Investment
import com.dzadafa.mywallet.data.InvestmentLog
import com.dzadafa.mywallet.data.InvestmentRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class InvestmentDetailViewModel(
    private val repository: InvestmentRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _investment = MutableLiveData<Investment?>()
    val investment: LiveData<Investment?> = _investment

    private val _logs = MutableLiveData<List<InvestmentLog>>()
    val logs: LiveData<List<InvestmentLog>> = _logs

    private val _dcaProgress = MutableLiveData<Pair<Double, Double>>()
    val dcaProgress: LiveData<Pair<Double, Double>> = _dcaProgress

    fun loadInvestment(id: Int) {
        viewModelScope.launch {
            _investment.value = repository.getInvestmentById(id)
            repository.getLogsForInvestment(id).collectLatest {
                _logs.value = it
            }
        }
        loadDcaProgress(id)
    }

    private fun loadDcaProgress(id: Int) {
        viewModelScope.launch {
            val inv = repository.getInvestmentById(id) ?: return@launch
            if (inv.targetMonthlyDca <= 0) {
                _dcaProgress.value = Pair(0.0, 0.0)
                return@launch
            }

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startOfMonth = calendar.time

            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.SECOND, -1)
            val endOfMonth = calendar.time

            repository.getMonthlyDcaProgress(id, startOfMonth, endOfMonth).collectLatest { totalBought ->
                val current = totalBought ?: 0.0
                _dcaProgress.value = Pair(current, inv.targetMonthlyDca)
            }
        }
    }

    fun updatePrice(newPrice: Double) {
        val currentInv = _investment.value ?: return
        viewModelScope.launch {
            val updatedInv = currentInv.copy(currentPrice = newPrice)
            repository.updateInvestment(updatedInv)
            _investment.value = updatedInv
        }
    }

    fun addTransaction(type: String, date: Date, units: Double, pricePerUnit: Double) {
        val currentInv = _investment.value ?: return
        viewModelScope.launch {
            val totalAmount = units * pricePerUnit
            val log = InvestmentLog(
                investmentId = currentInv.id, date = date, type = type,
                amountInvested = totalAmount, units = units, pricePerUnit = pricePerUnit
            )
            repository.insertLog(log)
            recalculateInvestmentState(currentInv.id, currentInv.currentPrice)
        }
    }

    fun deleteLog(log: InvestmentLog) {
        val currentInv = _investment.value ?: return
        viewModelScope.launch {
            repository.deleteLog(log)
            recalculateInvestmentState(currentInv.id, currentInv.currentPrice)
        }
    }

    private suspend fun recalculateInvestmentState(investmentId: Int, currentPrice: Double) {
        val allLogs = repository.getLogsForInvestmentSync(investmentId)

        var newHoldings = 0.0
        var totalCost = 0.0
        var newAvgPrice = 0.0

        for (log in allLogs) {
            if (log.type == "BUY") {
                totalCost += log.amountInvested
                newHoldings += log.units
            } else {
                newHoldings -= log.units
                if (newHoldings < 0) newHoldings = 0.0
                
                if (newHoldings > 0 && newAvgPrice > 0) {
                     totalCost = newHoldings * newAvgPrice
                } else {
                     totalCost = 0.0
                }
            }
            
            if (newHoldings > 0) {
                newAvgPrice = totalCost / newHoldings
            } else {
                newAvgPrice = 0.0
                totalCost = 0.0
            }
        }

        val updatedInv = repository.getInvestmentById(investmentId)?.copy(
            amountHeld = newHoldings,
            averageBuyPrice = newAvgPrice,
            currentPrice = currentPrice
        )

        if (updatedInv != null) {
            repository.updateInvestment(updatedInv)
            _investment.value = updatedInv
        }
    }

    fun deleteInvestment() {
         val currentInv = _investment.value ?: return
         viewModelScope.launch {
             repository.deleteInvestment(currentInv)
             _investment.value = null
         }
    }
}
