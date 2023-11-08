package com.example.currencyexchange.Repository

import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Models.TimeSeriesModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

enum class NetworkStatus{
    Available, Unavailable, Losing, Lost
}

interface CurrencyRepository {
    //Database
    val baseCurrency: Flow<CurrenciesDatabaseMain>
    val currencyListData: Flow<List<CurrenciesDatabaseDetailed>>
    val isInit: Flow<Boolean>

    suspend fun insertCurrencies(currency: CurrenciesDatabaseDetailed)
    suspend fun updateBaseCurrency(baseCurrency: CurrenciesDatabaseMain)
    suspend fun updateRates(currency: CurrenciesDatabaseDetailed)

    //Retrofit
    suspend fun getLatestRates(apiKey: String, baseCurrency: String): DataWrapper<LatestRates>
    suspend fun convertCurrency(apiKey: String, baseCurrency: String, wantedCurrency: String, amount: String): DataWrapper<ConversionModel>
    suspend fun getFluctuation(apiKey: String, startDate: String, endDate: String, baseCurrency: String, currencies: String): DataWrapper<FluctuationModel>
    suspend fun getHistorical(apiKey: String, baseCurrency: String, currencies: String, date: String): DataWrapper<HistoricalRatesModel>
    suspend fun getTimeSeriesData(apiKey: String, baseCurrency: String, currencies: String, startDate: String, endDate: String): DataWrapper<TimeSeriesModel>

    //Network services
    fun observeNetworkStatus(): Flow<NetworkStatus>

}