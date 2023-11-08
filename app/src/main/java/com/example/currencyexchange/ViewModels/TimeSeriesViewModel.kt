package com.example.currencyexchange.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.TimeSeriesModel
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
class TimeSeriesViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _TimeSeriesData = MutableLiveData<DataWrapper<TimeSeriesModel>?>()
    val timeSeriesData: LiveData<DataWrapper<TimeSeriesModel>?> get() = _TimeSeriesData

    fun clearResponse() {
        _TimeSeriesData.value = null
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

    val networkStatus: SharedFlow<DataWrapper<NetworkStatus>> =
        currencyRepository.observeNetworkStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchTimeSeriesData(
        baseCurrency: String,
        selectedCurrencies: String,
        startDate: String,
        endDate: String
    ) {
        if (baseCurrency.isEmpty() || selectedCurrencies.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) return

        viewModelScope.launch {
            val response = currencyRepository.getTimeSeriesData(
                baseCurrency = baseCurrency,
                currencies = selectedCurrencies,
                startDate = startDate,
                endDate = endDate,
                apiKey = BuildConfig.API_KEY
            )
            // to avoid forcing the response with '!!', use 'let' instead
            response.let {
                _TimeSeriesData.postValue(it)
            }
        }
    }
}