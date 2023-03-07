package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChangeBaseViewModel constructor(
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    /** Setup states for base currency*/
    private val _baseCurrencyState: MutableSharedFlow<DatabaseState> = MutableSharedFlow(replay = 1)
    val baseCurrency: SharedFlow<DatabaseState> get() = _baseCurrencyState

    /** Setup states for all currencies*/
    private val _allCurrencies: MutableSharedFlow<DatabaseState> = MutableSharedFlow(replay = 1)
    val currencies: SharedFlow<DatabaseState> = _allCurrencies

    fun getBaseCurrency() {
        viewModelScope.launch {
            databaseRepository.baseCurrency
                .catch { _baseCurrencyState.emit(DatabaseState.Error(it.cause)) }
                .collect { currency ->
                    _baseCurrencyState.emit(DatabaseState.Success(currency.baseCurr))
                }
        }
    }

    fun getAllCurrencies() {
        viewModelScope.launch {
            databaseRepository.allCurrencies
                .catch { _allCurrencies.emit(DatabaseState.Error(it.cause)) }
                .collect { currencies ->
                    Log.i(TAG, "getBaseCurrency: "+currencies::class.java.typeName)
                    Log.i(TAG, "getAllCurrencies: $currencies")
                    _allCurrencies.emit(DatabaseState.Success(currencies.map { it.currency}))
                }
            Log.i(TAG, "getAllCurrencies: $currencies")
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