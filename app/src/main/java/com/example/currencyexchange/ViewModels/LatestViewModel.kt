package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.Repository.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LatestViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _latestRatesCall = MutableLiveData<DataWrapper<LatestRates>>()
    val latestRates: LiveData<DataWrapper<LatestRates>> get() = _latestRatesCall

    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        currencyRepository.baseCurrency
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val currencyDataList: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
        currencyRepository.currencyListData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val isDbInit: SharedFlow<DataWrapper<Boolean>> =
        currencyRepository.isInit
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val internetConnection: SharedFlow<DataWrapper<NetworkStatus>> =
        currencyRepository.observeNetworkStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /** Launch coroutines and perform an api call
     * In case the call will be successfully, assign the response data to LiveData
     * In case the call will fail, display exception
     * Finally, insert/update map with currencies, and their rates in database, and update date, to let user know, from when the rates are  */
    fun fetchData(baseCurrency: String) {
        if (baseCurrency.isEmpty()) return

        viewModelScope.launch {
            try {
                val response = currencyRepository.getLatestRates(
                    baseCurrency = baseCurrency,
                    apiKey = BuildConfig.API_KEY
                )
                // to avoid forcing the response with '!!', use 'let' instead
                response.let {
                     _latestRatesCall.postValue(it)
                }
            } catch (exception: Exception) {
                _latestRatesCall.postValue(
                    DataWrapper.Error(
                        error = exception.message,
                        data = null
                    )
                )
            }
        }
    }

    fun insertCurrencies(currencyData: CurrenciesDatabaseDetailed) = viewModelScope.launch {
        try {
            currencyRepository.insertCurrencies(currencyData)
        } catch (exception: Exception) {
            Log.e(TAG, "insertCurrencies: couldn't insert currencies in view model. $exception")
        }
    }

    fun updateCurrencies(currencyData: CurrenciesDatabaseDetailed) = viewModelScope.launch {
        try {
            currencyRepository.updateRates(currencyData)
        } catch (exception: Exception) {
            Log.e(TAG, "updateCurrencies: couldn't update rates in view model. $exception")
        }
    }
}