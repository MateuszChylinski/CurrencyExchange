package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.API.ApiResult
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.IllegalArgumentException

class LatestViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    /** Setup states for base currency*/
    private val _baseCurrencyState: MutableSharedFlow<DatabaseState> = MutableSharedFlow(replay = 1)
    val baseCurrency: SharedFlow<DatabaseState> get() = _baseCurrencyState

    /** Setup states for all currencies*/
    private val _allCurrencies: MutableSharedFlow<DatabaseState> = MutableSharedFlow(replay = 1)
    val currencies: SharedFlow<DatabaseState> get() = _allCurrencies

    private val _latestRateStatus = MutableLiveData<ApiResult<LatestRates>>()
    val latestRates: LiveData<ApiResult<LatestRates>> get() = _latestRateStatus


    /** Launch coroutines with Dispatchers of IO
     *  Retrieve base currency from the database*/
    fun getBaseCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.baseCurrency
                .catch { _baseCurrencyState.emit(DatabaseState.Error(it.cause)) }
                .collect { currency ->
                    _baseCurrencyState.emit(DatabaseState.Success(currency.baseCurr))
                }
        }
    }

    /** Launch coroutines with Dispatchers of IO
     *  Insert new currencies to the database  */
    private fun addCurrency(currencyNames: List<CurrencyNamesModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            currencyNames.forEach { currency ->
                databaseRepository.addCurrency(currency)
            }
        }
    }

    /** Launch coroutines with Dispatchers of IO
     *  Make an api call to get latest rates of specific base currency */
    fun fetchData(baseCurrency: String) {
        viewModelScope.launch {
            try {
                val response = apiRepository.getLatestRates(baseCurrency, BuildConfig.API_KEY)
                _latestRateStatus.value = response
            } catch (exception: IOException) {
                Log.i(
                    TAG,
                    "Failed to fetch data from repository for latest rates.\n${exception.message}"
                )
            } finally {
                val currencyNames: MutableList<CurrencyNamesModel> = mutableListOf()
                latestRates.value?.data?.latestRates?.keys?.forEach { currency ->
                    currencyNames.add(CurrencyNamesModel(currency))
                }
                addCurrency(currencyNames)
            }
        }
    }
}

class LatestFactory(
    private val retrofitRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LatestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LatestViewModel(retrofitRepository, databaseRepository) as T
        }
        throw IllegalArgumentException("Unknown retrofit ViewModel")
    }
}
