package com.example.currencyexchange.Repository

import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.BuildConfig


class CurrencyRetrofitRepository constructor(private val apiServices: ApiServices) {

    fun fetchLatestRates() = apiServices.getRatesData("EUR", BuildConfig.API_KEY)
    fun fetchFluctuation(startDate: String, endDate:String, baseCurrency: String, symbols: String) = apiServices.getFluctuationData(startDate, endDate, baseCurrency, symbols, BuildConfig.API_KEY)
    fun convertCurrency() = apiServices.convertCurrency("PLN", "EUR", 100.0, BuildConfig.API_KEY)
}