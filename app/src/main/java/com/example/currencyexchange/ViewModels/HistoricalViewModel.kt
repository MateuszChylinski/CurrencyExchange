package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.Repository.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HistoricalViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _historical = MutableLiveData<DataWrapper<HistoricalRatesModel>?>()
    val historicalData: LiveData<DataWrapper<HistoricalRatesModel>?> get() = _historical

    fun clearResponse() {
        _historical.value = null
    }

    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        currencyRepository.baseCurrency
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val allCurrencies: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
        currencyRepository.currencyListData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val networkState: SharedFlow<DataWrapper<NetworkStatus>> =
        currencyRepository.observeNetworkStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchHistoricalData(baseCurrency: String, selectedCurrencies: String, date: String) {
        if (baseCurrency.isEmpty() || selectedCurrencies.isEmpty() || date.isEmpty()) return

        viewModelScope.launch {
            val response = currencyRepository.getHistorical(
                baseCurrency = baseCurrency,
                currencies = selectedCurrencies,
                date = date,
                apiKey = BuildConfig.API_KEY
            )
            // to avoid forcing the response with '!!', use 'let' instead
            response.let {
                _historical.postValue(it)
            }
        }
    }
}