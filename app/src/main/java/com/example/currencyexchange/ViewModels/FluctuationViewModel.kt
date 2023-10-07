package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.FluctuationModel
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
import javax.inject.Inject

@HiltViewModel
class FluctuationViewModel @Inject constructor(
    private val apiRepository: RetrofitRepositoryImplementation,
    private val databaseRepository: DatabaseRepositoryImplementation,
    private val networkStatus: NetworkObserverImplementation
) : ViewModel() {
    private val _fluctuation = MutableLiveData<DataWrapper<FluctuationModel>?>()
    val fluctuationResponse: LiveData<DataWrapper<FluctuationModel>?> get() = _fluctuation

    fun clearApiResponse(){
        _fluctuation.value = null
    }

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

    fun fetchFluctuation(
        baseCurrency: String,
        selectedCurrencies: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                val response = apiRepository.getFluctuation(
                    baseCurrency = baseCurrency,
                    currencies = selectedCurrencies,
                    startDate = startDate,
                    endDate = endDate,
                    apiKey = BuildConfig.API_KEY
                )
                if (response.isSuccessful) {
                    response.body()?.let { _fluctuation.postValue(DataWrapper.Success(it)) }

                } else {
                    Log.e(
                        TAG,
                        "fetchFluctuation: response from the server was not successful. Response code: ${response.code()}"
                    )
                }
            } catch (exception: java.net.SocketTimeoutException) {
                _fluctuation.postValue(DataWrapper.Error(data = null, error = exception.message))
            }
        }
    }
}