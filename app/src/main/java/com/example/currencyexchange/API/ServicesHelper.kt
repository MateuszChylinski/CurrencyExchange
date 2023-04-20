package com.example.currencyexchange.API

import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import retrofit2.Response

interface ServicesHelper {
    suspend fun getLatestRates(apiKey: String, baseCurrency: String): Response<LatestRates>?
    suspend fun getFluctuation(apiKey: String, startDate: String, endDate: String, baseCurrency: String, currencies: String): Response<FluctuationModel>
    suspend fun getHistorical(apiKey: String, baseCurrency: String, currencies: String, date: String): Response<HistoricalRatesModel>
    suspend fun convertCurrency(apiKey: String, baseCurrency: String, wantedCurrency: String, amount: Int): Response<ConversionModel>
}