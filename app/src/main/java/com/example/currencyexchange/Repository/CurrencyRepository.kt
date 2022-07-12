package com.example.currencyexchange.Repository

import com.example.currencyexchange.APIs.ApiServices
import com.example.currencyexchange.BuildConfig


class CurrencyRepository constructor(private val apiServices: ApiServices) {
    fun fetchLatestRates() = apiServices.getRatesData("EUR", BuildConfig.API_KEY)
}