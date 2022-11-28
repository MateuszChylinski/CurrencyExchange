package com.example.currencyexchange.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.launch

class ChangeBaseViewModel constructor(
    private val currencyDatabaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    val baseCurrency = currencyDatabaseRepository.baseCurrency.asLiveData()
    val currencyList = currencyDatabaseRepository.allCurrencies.asLiveData()

     fun updateBaseCurrency(selectedCurrency: String) {
        viewModelScope.launch {
            val newBase = currencyDatabaseRepository.updateBaseCurrency(
                BaseCurrencyModel(
                    1,
                    selectedCurrency
                )
            )
        }
    }
}

class ChangeBaseFactory(
    private val currencyDatabaseRepository: CurrencyDatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeBaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangeBaseViewModel(currencyDatabaseRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN VIEW MODEL CAST FROM CHANGE BASE")
    }
}