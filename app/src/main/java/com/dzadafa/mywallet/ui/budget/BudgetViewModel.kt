package com.dzadafa.mywallet.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dzadafa.mywallet.data.Budget
import com.dzadafa.mywallet.data.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repository: BudgetRepository,
    application: Application
) : AndroidViewModel(application) {

    val allBudgets: LiveData<List<Budget>> = repository.allBudgets.asLiveData()
    
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    fun insert(category: String, limit: Double) {
        if (category.isBlank()) {
            _toastMessage.value = "Category name cannot be empty"
            return
        }
        viewModelScope.launch {
            try {
                
                val formattedCategory = category.trim().replaceFirstChar { it.uppercase() }
                repository.insert(Budget(category = formattedCategory, limitAmount = limit))
                _toastMessage.postValue("Category added")
            } catch (e: Exception) {
                _toastMessage.postValue("Error: Category '$category' might already exist")
            }
        }
    }

    fun update(budget: Budget) {
        viewModelScope.launch {
            repository.update(budget)
            _toastMessage.postValue("Updated")
        }
    }

    fun delete(budget: Budget) {
        viewModelScope.launch {
            repository.delete(budget)
            _toastMessage.postValue("Deleted")
        }
    }
}
