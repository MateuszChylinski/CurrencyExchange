package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Repository.Implementation.DatabaseRepositoryImplementation
import com.example.currencyexchange.API.ServicesHelperImplementation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LatestViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepositoryImplementation,
    private val retrofitRepository: ServicesHelperImplementation
) : ViewModel() {

    private var doesContainData = false
    private val _latestRatesCall = MutableLiveData<DataWrapper<LatestRates>>()
    val latestRates: LiveData<DataWrapper<LatestRates>> get() = _latestRatesCall



    val baseCurrencyState: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        databaseRepository.baseCurrency
            .catch { DataWrapper.Error(it.message)}
            .map { DataWrapper.Success(it)}
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val currenciesDataState: SharedFlow<DataWrapper<CurrenciesDatabaseDetailed>> =
        databaseRepository.currencyData
            .catch { DataWrapper.Error(it.message) }
            .map { DataWrapper.Success(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    /** Launch coroutines and perform an api call
     * In case the call will be successfully, assign the response data to LiveData
     * In case the call will fail, display exception
     * Finally, insert/update map with currencies, and their rates in database, and update date, to let user know, from when the rates are  */
    fun fetchData(baseCurrency: String) = viewModelScope.launch {
        val response = retrofitRepository.getLatestRates(
            baseCurrency = baseCurrency,
            apiKey = BuildConfig.API_KEY
        )
        try {
            if (response?.isSuccessful == true) {
                _latestRatesCall.postValue(DataWrapper.Success(response.body()!!))
            }
        } catch (exception: Exception) {
            _latestRatesCall.postValue(DataWrapper.Error(error = exception.message))
        } finally {
            insertCurrencies(CurrenciesDatabaseDetailed(currencyData = response?.body()?.latestRates!!))
        }
    }


    /** Check if database contains any currency rates, and perform appropriate operation
     *  If database doesn't contains any data about currency rates, insert given rates.
     *  If database do contain currency data, update it.    */
    private fun insertCurrencies(currencyData: CurrenciesDatabaseDetailed) = viewModelScope.launch {
        try {
            if (doesContainData) {
                databaseRepository.updateRates(currencyData)
            } else {
                databaseRepository.insertCurrencies(currencyData)
            }
        } catch (exception: IOException) {
            Log.i(TAG, "insertCurrencies: couldn't insert/update currencies. $exception")
        }
    }
}
// TODO - check internet connection to perform appropriate operation
//  if there is internet connection - insert present rates and date to the database
//  if there's not internet connection, display recent inserted rates
