package com.example.currencyexchange.ViewModels

import androidx.lifecycle.*
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.Models.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class CurrencyDatabaseViewModel(private val currencyDatabaseRepository: CurrencyDatabaseRepository) :
    ViewModel() {
    val currencyNames = mutableListOf<CurrencyDatabaseModel>()

    fun insertNewCurrency(currencyDatabaseModel: CurrencyDatabaseModel) = viewModelScope.launch {
        currencyDatabaseRepository.insertNewCurrency(currencyDatabaseModel)
    }
    //  Since in fluctuation fragment, there are two adapters: one for spinner (which can be used to pick base currency for callback),
//  and second for ListView Which need option to pick 'all currencies' program need two list with all currencies.
//  The only one difference is that the list for spinner doesn't have option to pick all currencies
    val fluctuationRatesForSpinner: LiveData<List<CurrencyDatabaseModel>> = currencyDatabaseRepository.allCurrencyNamesModel.asLiveData()
    val allCurrencies: LiveData<List<CurrencyDatabaseModel>> = currencyDatabaseRepository.allCurrencyNamesModel.asLiveData()
}


class CurrencyDatabaseFactory(private val currencyDatabaseRepository: CurrencyDatabaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CurrencyDatabaseViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return CurrencyDatabaseViewModel(currencyDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class DB")
    }
}