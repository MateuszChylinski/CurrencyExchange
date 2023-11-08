package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
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
class ConversionViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _exchangeState = MutableLiveData<DataWrapper<ConversionModel>?>()
    val exchangeResult: MutableLiveData<DataWrapper<ConversionModel>?> get() = _exchangeState

    private val _convertTo = MutableLiveData<String?>()
    val convertTo: LiveData<String?> get() = _convertTo

    private val _convertedAmount = MutableLiveData<Double?>()
    val convertedAmount: LiveData<Double?> get() = _convertedAmount


    fun clearResponse() {
        _exchangeState.value = null
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
            .catch { DataWrapper.Error(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val databaseState: SharedFlow<DataWrapper<Boolean>> =
        currencyRepository.isInit
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val currencyData: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
        currencyRepository.currencyListData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun exchangeCurrency(baseCurrency: String, selectedCurrency: String, amount: String) {
        if (baseCurrency.isEmpty() || selectedCurrency.isEmpty() || amount.isEmpty()) return

        viewModelScope.launch {
            val response = currencyRepository.convertCurrency(
                baseCurrency = baseCurrency,
                wantedCurrency = selectedCurrency,
                amount = amount,
                apiKey = BuildConfig.API_KEY
            )
            response.let {
                _exchangeState.postValue(it)
            }
        }
    }
}
