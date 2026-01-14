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
import java.util.Date

class InvestmentDetailViewModel(
    private val repository: InvestmentRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _investment = MutableLiveData<Investment?>()
    val investment: LiveData<Investment?> = _investment

    private val _logs = MutableLiveData<List<InvestmentLog>>()
    val logs: LiveData<List<InvestmentLog>> = _logs

    fun loadInvestment(id: Int) {
        viewModelScope.launch {
            val data = repository.getInvestmentById(id)
            _investment.value = data

            repository.getLogsForInvestment(id).collectLatest {
                _logs.value = it
            }
        }
    }

    fun addTransaction(type: String, date: Date, units: Double, pricePerUnit: Double) {
        val currentInv = _investment.value ?: return

        viewModelScope.launch {

            val totalAmount = units * pricePerUnit
            val log = InvestmentLog(
                investmentId = currentInv.id,
                date = date,
                type = type,
                amountInvested = totalAmount,
                units = units,
                pricePerUnit = pricePerUnit
            )
            repository.insertLog(log)

            var newAmountHeld = currentInv.amountHeld
            var newAvgPrice = currentInv.averageBuyPrice

            if (type == "BUY") {

                val currentTotalCost = currentInv.amountHeld * currentInv.averageBuyPrice
                val newTotalCost = currentTotalCost + totalAmount
                newAmountHeld += units

                if (newAmountHeld > 0) {
                    newAvgPrice = newTotalCost / newAmountHeld
                }
            } else {

                newAmountHeld -= units
                if (newAmountHeld < 0) newAmountHeld = 0.0
            }

            val updatedInv = currentInv.copy(
                amountHeld = newAmountHeld,
                averageBuyPrice = newAvgPrice,

                currentPrice = pricePerUnit 
            )

            repository.updateInvestment(updatedInv)
            _investment.value = updatedInv
        }
    }

    fun deleteInvestment() {
         val currentInv = _investment.value ?: return
         viewModelScope.launch {
             repository.deleteInvestment(currentInv)
         }
    }
}
