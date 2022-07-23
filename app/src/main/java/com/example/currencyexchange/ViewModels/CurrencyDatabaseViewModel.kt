package com.example.currencyexchange.ViewModels

import androidx.lifecycle.*
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class CurrencyDatabaseViewModel(private val currencyDatabaseRepository: CurrencyDatabaseRepository) :
    ViewModel() {
    val currencyNames = mutableListOf<CurrencyDatabaseModel>()

    fun populateNames(name: String){
        currencyNames.add(CurrencyDatabaseModel(name))
    }

    fun insertNewCurrency(currencyDatabaseModel: CurrencyDatabaseModel) = viewModelScope.launch {
        currencyDatabaseRepository.insertNewCurrency(currencyDatabaseModel)
    }

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