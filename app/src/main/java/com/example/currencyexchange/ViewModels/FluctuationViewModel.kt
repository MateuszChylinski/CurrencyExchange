package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.Repository.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FluctuationViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {
    private val _fluctuation = MutableLiveData<DataWrapper<FluctuationModel>?>()
    val fluctuationResponse: LiveData<DataWrapper<FluctuationModel>?> get() = _fluctuation

    fun clearApiResponse() {
        _fluctuation.value = null
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

    fun fetchFluctuation(
        baseCurrency: String,
        selectedCurrencies: String,
        startDate: String,
        endDate: String
    ) {
        if (baseCurrency.isEmpty() || selectedCurrencies.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) return

        viewModelScope.launch {
            val response = currencyRepository.getFluctuation(
                baseCurrency = baseCurrency,
                currencies = selectedCurrencies,
                startDate = startDate,
                endDate = endDate,
                apiKey = BuildConfig.API_KEY
            )
            // to avoid forcing the response with '!!', use 'let' instead
            response.let {
                _fluctuation.postValue(it)
            }
        }
    }
}