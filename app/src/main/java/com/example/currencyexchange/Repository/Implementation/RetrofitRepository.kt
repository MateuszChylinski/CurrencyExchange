package com.example.currencyexchange.Repository.Implementation

import com.example.currencyexchange.API.ServicesHelper
import javax.inject.Inject

class RetrofitRepository @Inject constructor(private val servicesHelper: ServicesHelper) {
    suspend fun getLatestRates(apiKey: String, baseCurrency: String) = servicesHelper.getLatestRates(apiKey = apiKey, baseCurrency = baseCurrency)
}