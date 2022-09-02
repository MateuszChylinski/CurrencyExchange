package com.example.currencyexchange.ViewModels

import androidx.lifecycle.*
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class CurrencyDatabaseViewModel(private val currencyDatabaseRepository: CurrencyDatabaseRepository) :
    ViewModel() {

    //  List of currency names TODO
    val currencyNames = currencyDatabaseRepository.allCurrencies

    //  Base currency
    val baseCurrency = currencyDatabaseRepository.baseCurrency.asLiveData()

    //  Insert new currency into the database
    fun insertNewCurrency(currencyNamesModel: CurrencyNamesModel) = viewModelScope.launch {
        currencyDatabaseRepository.insertNewCurrency(currencyNamesModel)
    }

    // Update base currency
    fun updateBaseCurrency(currencyNamesModel: CurrencyNamesModel) = viewModelScope.launch {
        currencyDatabaseRepository.updateBaseCurrency(currencyNamesModel)
    }


    //  Since in fluctuation fragment, there are two adapters: one for spinner (which can be used to pick base currency for callback),
//  and second for ListView Which need option to pick 'all currencies' program need two list with all currencies.
//  The only one difference is that the list for spinner doesn't have option to pick all currencies

//    val fluctuationRatesForSpinner: LiveData<List<CurrencyNamesModel>> = currencyDatabaseRepository.allCurrencyNamesModel.asLiveData()
//    val allCurrencies: LiveData<List<CurrencyNamesModel>> = currencyDatabaseRepository.allCurrencyNamesModel.asLiveData()

//  BASE CURR
//    val baseCurrency: LiveData<BaseCurrencyModel> = currencyDatabaseRepository.baseCurrency.asLiveData()
//
//    fun updateCurrency(baseCurrencyModel: BaseCurrencyModel) {
//        viewModelScope.launch(Dispatchers.IO) {
//            currencyDatabaseRepository.updateBaseCurrency(baseCurrencyModel)
//        }
//    }
}


class CurrencyDatabaseFactory(private val currencyDatabaseRepository: CurrencyDatabaseRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyDatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyDatabaseViewModel(currencyDatabaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class DB")
    }
}