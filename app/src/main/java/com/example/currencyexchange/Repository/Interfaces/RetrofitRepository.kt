package com.example.currencyexchange.Repository.Interfaces

import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import retrofit2.Response

interface RetrofitRepository {
    suspend fun getLatestRates(apiKey: String, baseCurrency: String): Response<LatestRates>
    suspend fun convertCurrency(apiKey: String, baseCurrency: String, wantedCurrency: String, amount: String): Response<ConversionModel>
    suspend fun getFluctuation(apiKey: String, startDate: String, endDate: String, baseCurrency: String, currencies: String): Response<FluctuationModel>
    suspend fun getHistorical(apiKey: String, baseCurrency: String, currencies: String, date: String): Response<HistoricalRatesModel>
}