package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.NetworkDetection.NetworkObserver
import com.example.currencyexchange.NetworkDetection.NetworkObserverImplementation
import com.example.currencyexchange.Repository.Implementation.DatabaseRepositoryImplementation
import com.example.currencyexchange.Repository.Implementation.RetrofitRepositoryImplementation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LatestViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepositoryImplementation,
    private val retrofitRepository: RetrofitRepositoryImplementation,
    private val networkObserver: NetworkObserverImplementation
) : ViewModel() {

    private val _latestRatesCall = MutableLiveData<DataWrapper<LatestRates>>()
    val latestRates: LiveData<DataWrapper<LatestRates>> get() = _latestRatesCall

    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        databaseRepository.baseCurrency
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val currencyDataList: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
        databaseRepository.currencyListData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val isDbInit: SharedFlow<DataWrapper<Boolean>> =
        databaseRepository.isInit
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val internetConnection: SharedFlow<DataWrapper<NetworkObserver.NetworkStatus>> =
        networkObserver.observeStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    /** Launch coroutines and perform an api call
     * In case the call will be successfully, assign the response data to LiveData
     * In case the call will fail, display exception
     * Finally, insert/update map with currencies, and their rates in database, and update date, to let user know, from when the rates are  */
    fun fetchData(baseCurrency: String) =
        viewModelScope.launch {
            try {

                val response = retrofitRepository.getLatestRates(
                    baseCurrency = baseCurrency,
                    apiKey = BuildConfig.API_KEY
                )
                if (response.isSuccessful) {
                    _latestRatesCall.postValue(DataWrapper.Success(response.body()!!))
                }
            } catch (exception: java.net.SocketTimeoutException) {
                _latestRatesCall.postValue(
                    DataWrapper.Error(
                        error = exception.message,
                        data = null
                    )
                )
            }
        }

    fun insertCurrencies(currencyData: CurrenciesDatabaseDetailed) = viewModelScope.launch {
        try {
            databaseRepository.insertCurrencies(currencyData)
        } catch (exception: IOException) {
            Log.e(TAG, "insertCurrencies: couldn't insert currencies in view model. $exception")
        }
    }

    fun updateCurrencies(currencyData: CurrenciesDatabaseDetailed) = viewModelScope.launch {
        try {
            databaseRepository.updateRates(currencyData)
        } catch (exception: Exception) {
            Log.e(TAG, "updateCurrencies: couldn't update rates in view model. $exception")
        }
    }
}