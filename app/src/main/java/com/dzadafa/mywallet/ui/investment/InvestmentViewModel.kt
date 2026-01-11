package com.dzadafa.mywallet.ui.investment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.data.Investment
import com.dzadafa.mywallet.data.InvestmentRepository
import kotlinx.coroutines.launch

class InvestmentViewModel(
    private val repository: InvestmentRepository,
    application: Application
) : AndroidViewModel(application) {

    val allInvestments: LiveData<List<Investment>> = repository.allInvestments.asLiveData()

    val totalPortfolioValue: LiveData<Double> = allInvestments.map { list ->
        list.sumOf { it.getCurrentValue() }
    }

    val totalProfitLoss: LiveData<Pair<Double, Double>> = allInvestments.map { list ->
        val totalCost = list.sumOf { it.getTotalCost() }
        val totalValue = list.sumOf { it.getCurrentValue() }
        val plAmount = totalValue - totalCost
        val plPercent = if (totalCost > 0) (plAmount / totalCost) * 100 else 0.0
        Pair(plAmount, plPercent)
    }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    fun addInvestment(name: String, type: String, currentPrice: Double, dcaTarget: Double) {
        if (name.isBlank()) {
            _toastMessage.value = "Name required"
            return
        }
        viewModelScope.launch {
            val newInvestment = Investment(
                name = name,
                type = type,
                amountHeld = 0.0,      

                averageBuyPrice = 0.0, 

                currentPrice = currentPrice,
                targetMonthlyDca = dcaTarget
            )
            repository.insertInvestment(newInvestment)
            _toastMessage.postValue("Asset added")
        }
    }
}
