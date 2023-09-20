package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.TimeSeriesModel
import com.example.currencyexchange.NetworkDetection.NetworkObserver
import com.example.currencyexchange.NetworkDetection.NetworkObserverImplementation
import com.example.currencyexchange.Repository.Implementation.DatabaseRepositoryImplementation
import com.example.currencyexchange.Repository.Implementation.RetrofitRepositoryImplementation
import com.example.currencyexchange.Singletons.DataModifier
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
    private val databaseRepository: DatabaseRepositoryImplementation,
    private val retrofitRepository: RetrofitRepositoryImplementation,
    private val networkObserver: NetworkObserverImplementation,
) : ViewModel() {

    private val _TimeSeriesData = MutableLiveData<DataWrapper<TimeSeriesModel>>()
    val timeSeriesData: LiveData<DataWrapper<TimeSeriesModel>> get() = _TimeSeriesData

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

    val networkStatus: SharedFlow<DataWrapper<NetworkObserver.NetworkStatus>> =
        networkObserver.observeStatus()
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    fun fetchTimeSeriesData(
        basCurrency: String,
        selectedCurrencies: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                val response = retrofitRepository.getTimeSeriesData(
                    baseCurrency = basCurrency,
                    currencies = selectedCurrencies,
                    startDate = startDate,
                    endDate = endDate,
                    apiKey = BuildConfig.API_KEY
                )
                if (response.isSuccessful) {
                    _TimeSeriesData.postValue(DataWrapper.Success(response.body()!!))
                } else {
                    Log.e(
                        TAG,
                        "fetchTimeSeriesData: Couldn't get response from api ${response.code()}",
                    )
                }
            } catch (exception: java.net.SocketTimeoutException) {
                _TimeSeriesData.postValue(DataWrapper.Error(null, exception.message))
            }
        }
    }
}


