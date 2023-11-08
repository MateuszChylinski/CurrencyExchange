package com.example.currencyexchange.Repository

import android.net.ConnectivityManager
import android.net.Network
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Models.TimeSeriesModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class CurrencyRepositoryImplementation @Inject constructor(
    private val currencyDAO: CurrencyDAO,
    private val apiServices: ApiServices,
    private val connectivityManager: ConnectivityManager
): CurrencyRepository {
    override val baseCurrency: Flow<CurrenciesDatabaseMain>
        get() = currencyDAO.getBaseCurrency()

    override val currencyListData: Flow<List<CurrenciesDatabaseDetailed>>
        get() = currencyDAO.getCurrencyListData()
    override val isInit: Flow<Boolean>
        get() = currencyDAO.checkIfInit()

    override suspend fun insertCurrencies(currency: CurrenciesDatabaseDetailed) {
        currencyDAO.insertCurrencyData(currency)
    }

    override suspend fun updateBaseCurrency(baseCurrency: CurrenciesDatabaseMain) {
        currencyDAO.updateBaseCurrency(baseCurrency.baseCurrency)
    }

    override suspend fun updateRates(currency: CurrenciesDatabaseDetailed) {
        currencyDAO.updateCurrenciesData(currency)

    }

    override suspend fun getLatestRates(
        apiKey: String,
        baseCurrency: String
    ): DataWrapper<LatestRates> {
        return try {
            val response = apiServices.getLatestRates(
                apikey = apiKey,
                base = baseCurrency
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let DataWrapper.Success(it)
                } ?: DataWrapper.Error(
                    null,
                    "Error occurred in repository implementation. Code ${response.code()}. Message ${response.message()}"
                )
            } else {
                DataWrapper.Error(
                    null,
                    "Error occurred while trying to get latest rates in repository. Call was NOT successful: ${response.code()}"
                )
            }
        } catch (exception: Exception) {
            DataWrapper.Error(null, "Couldn't reach the server while trying to get latest rates")
        }
    }

    override suspend fun convertCurrency(
        apiKey: String,
        baseCurrency: String,
        wantedCurrency: String,
        amount: String
    ): DataWrapper<ConversionModel> {
        return try {
            val response = apiServices.convertCurrency(
                apiKey = apiKey,
                from = baseCurrency,
                to = wantedCurrency,
                amount = amount
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let DataWrapper.Success(it)
                } ?: DataWrapper.Error(
                    null,
                    "Error occurred in repository implementation. Code ${response.code()}. Message ${response.message()}"
                )
            } else {
                DataWrapper.Error(
                    null,
                    "Error occurred while trying to convert currency in repository. Call was NOT successful: ${response.code()}"
                )
            }
        } catch (exception: Exception) {
            DataWrapper.Error(null, "Couldn't reach the server while trying to convert currency")
        }
    }

    override suspend fun getFluctuation(
        apiKey: String,
        startDate: String,
        endDate: String,
        baseCurrency: String,
        currencies: String
    ): DataWrapper<FluctuationModel> {
        return try {
            val response = apiServices.getFluctuationData(
                apikey = apiKey,
                startDate = startDate,
                endDate = endDate,
                base = baseCurrency,
                symbols = currencies
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let DataWrapper.Success(it)
                } ?: DataWrapper.Error(
                    null,
                    "Error occurred in repository implementation. Code ${response.code()}. Message ${response.message()}"
                )
            } else {
                DataWrapper.Error(
                    null,
                    "Error occurred while trying to get fluctuation data in repository. Call was NOT successful: ${response.code()}"
                )
            }
        } catch (exception: Exception) {
            DataWrapper.Error(
                null,
                "Couldn't reach the server while trying to get fluctuation data"
            )
        }
    }

    override suspend fun getHistorical(
        apiKey: String,
        baseCurrency: String,
        currencies: String,
        date: String
    ): DataWrapper<HistoricalRatesModel> {
        return try {
            val response = apiServices.getHistoricalData(
                apiKey = apiKey,
                baseCurrency = baseCurrency,
                symbols = currencies,
                date = date
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let DataWrapper.Success(it)
                } ?: DataWrapper.Error(
                    null,
                    "Error occurred in repository implementation. Code ${response.code()}. Message ${response.message()}"
                )
            } else {
                DataWrapper.Error(
                    null,
                    "Error occurred while trying to get historical data in repository. Call was NOT successful: ${response.code()}"
                )
            }
        } catch (exception: Exception) {
            DataWrapper.Error(null, "Couldn't reach the server while trying to get historical data")
        }
    }

    override suspend fun getTimeSeriesData(
        apiKey: String,
        baseCurrency: String,
        currencies: String,
        startDate: String,
        endDate: String
    ): DataWrapper<TimeSeriesModel> {
        return try {
            val response = apiServices.getTimeSeries(
                apikey = apiKey,
                base = baseCurrency,
                symbols = currencies,
                startDate = startDate,
                endDate = endDate
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let DataWrapper.Success(it)
                } ?: DataWrapper.Error(
                    null,
                    "Error occurred in repository implementation. Code ${response.code()}. Message ${response.message()}"
                )
            } else {
                DataWrapper.Error(
                    null,
                    "Error occurred while trying to get time series data in repository. Call was NOT successful: ${response.code()}"
                )
            }
        } catch (exception: Exception) {
            DataWrapper.Error(
                null,
                "Couldn't reach the server while trying to get time series data"
            )
        }
    }

    override fun observeNetworkStatus(): Flow<NetworkStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch {
                        send(NetworkStatus.Available)
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(NetworkStatus.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(NetworkStatus.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(NetworkStatus.Unavailable) }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}