package com.example.currencyexchange.Repository

import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.BuildConfig

class CurrencyRetrofitRepository constructor(private val apiServices: ApiServices) {

    fun fetchLatestRates(baseCurrency: String) =
        apiServices.getRatesData(baseCurrency, BuildConfig.API_KEY)

    fun fetchFluctuation(
        startDate: String,
        endDate: String,
        baseCurrency: String,
        symbols: String
    ) = apiServices.getFluctuationData(
        startDate,
        endDate,
        baseCurrency,
        symbols,
        BuildConfig.API_KEY
    )

    fun convertCurrency(from: String, to: String, amount: String) =
        apiServices.convertCurrency(from, to, amount, BuildConfig.API_KEY)

    fun fetchHistoricalData(data: String, symbols: String, base: String) =
        apiServices.getHistoricalData(data, symbols, base, BuildConfig.API_KEY)

}