package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeBaseViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val baseCurrencyState: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        currencyRepository.baseCurrency
            .catch { DataWrapper.Error(it.message) }
            .map { DataWrapper.Success(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val currencyNames: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
        currencyRepository.currencyListData
            .catch { DataWrapper.Error(it.message) }
            .map { DataWrapper.Success(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    fun updateBaseCurrency(currency: CurrenciesDatabaseMain) {
        viewModelScope.launch {
            try {
                currencyRepository.updateBaseCurrency(currency)
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "updateBaseCurrency: Failed to update base currency in $TAG\n${exception.message}"
                )
            }
        }
    }
}
