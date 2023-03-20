package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.API.ApiResult
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.IllegalArgumentException

class LatestViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {
    var isRatesPresent = false

    val baseCurrencyState: SharedFlow<DatabaseState<CurrenciesDatabaseMain>> =
        databaseRepository.baseCurrency
            .catch {
                DatabaseState.Error(it.message)
            }
            .map {
                DatabaseState.Success(it)

            }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    val currenciesDataState: SharedFlow<DatabaseState<CurrenciesDatabaseDetailed>> =
        databaseRepository.currencyData
            .catch { DatabaseState.Error(it.message) }
            .map { DatabaseState.Success(it) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    private val _latestRateStatus = MutableLiveData<ApiResult<LatestRates>>()
    val latestRates: LiveData<ApiResult<LatestRates>> get() = _latestRateStatus


    /** Launch coroutines and perform an api call
     * In case the call will be successfully, assign the response data to LiveData
     * In case the call will fail, display exception
     * Finally, insert/update map with currencies, and their rates in database, and update date, to let user know, from when the rates are  */
    fun fetchData(baseCurrency: String) {
        viewModelScope.launch {
            try {
                val response = apiRepository.getLatestRates(baseCurrency, BuildConfig.API_KEY)
                _latestRateStatus.value = response
                Log.i(TAG, " onCreateView fetchData: *********** (PARAMETER) $baseCurrency || (API) ${response.data?.baseCurrency} ****************\n" +
                        "${_latestRateStatus.value?.data?.latestRates}\n****************************")

            } catch (exception: IOException) {
                Log.e(
                    TAG,
                    "Failed to fetch data from repository for latest rates.\n${exception.message}"
                )
            } finally {
//                Insert map with currencies, and their rates into database
                if (isRatesPresent) {
                    databaseRepository.updateCurrencyData(latestRates.value?.data?.latestRates!!)
                } else {
                    databaseRepository.insertCurrencyData(
                        CurrenciesDatabaseDetailed(0, latestRates.value?.data?.latestRates!!)
                    )
                    isRatesPresent = true
                }

                /** Update date of rates.
                In case there will be no internet connection, program will display rates from the last time,
                when program successfully retrieved data from the server */
                databaseRepository.updateRatesDate(latestRates.value?.data?.date)
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
