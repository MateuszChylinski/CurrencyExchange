package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.NetworkDetection.NetworkObserver
import com.example.currencyexchange.NetworkDetection.NetworkObserverImplementation
import com.example.currencyexchange.Repository.Implementation.DatabaseRepositoryImplementation
import com.example.currencyexchange.Repository.Implementation.RetrofitRepositoryImplementation
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
    private val retrofitRepository: RetrofitRepositoryImplementation,
    private val databaseRepository: DatabaseRepositoryImplementation,
    private val networkStatus: NetworkObserverImplementation
) : ViewModel() {

    private val _historical = MutableLiveData<DataWrapper<HistoricalRatesModel?>>()
    val historicalData: LiveData<DataWrapper<HistoricalRatesModel?>> get() = _historical

    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        databaseRepository.baseCurrency
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val allCurrencies: SharedFlow<DataWrapper<CurrenciesDatabaseDetailed>> =
        databaseRepository.currencyData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val networkState: SharedFlow<DataWrapper<NetworkObserver.NetworkStatus>> =
        networkStatus.observeStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchHistoricalData(baseCurrency: String, selectedCurrencies: String, date: String) {
        viewModelScope.launch {
            try {
                val response = retrofitRepository.getHistorical(
                    baseCurrency = baseCurrency,
                    currencies = selectedCurrencies,
                    date = date,
                    apiKey = BuildConfig.API_KEY
                )
                // to avoid forcing the response with '!!', use 'let' instead
                response.let {
                    if (it.isSuccessful) {
                        _historical.postValue(DataWrapper.Success(it.body()))
                    } else {
                        Log.e(
                            TAG,
                            "ViewModel: Response is NOT successful. Code: ${response.code()}"
                        )
                    }
                }
            } catch (exception: Exception) {
                _historical.postValue(DataWrapper.Error(error = exception.message, data = null))
            }
        }
    }
}

