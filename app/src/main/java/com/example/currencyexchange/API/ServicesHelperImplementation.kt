package com.example.currencyexchange.API

import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import retrofit2.Response
import javax.inject.Inject

class ServicesHelperImplementation @Inject constructor(private val services: ApiServices) :
    ServicesHelper {

    override suspend fun getLatestRates(
        apiKey: String,
        baseCurrency: String
    ): Response<LatestRates>? =
        services.getLatestRates(apikey = apiKey, base = baseCurrency)

    override suspend fun convertCurrency(
        baseCurrency: String,
        wantedCurrency: String,
        amount: String,
        apiKey: String
    ): Response<ConversionModel> =
        services.convertCurrency(
            from = baseCurrency,
            to = wantedCurrency,
            amount = amount,
            apiKey = apiKey
        )

    override suspend fun getFluctuation(
        apiKey: String,
        startDate: String,
        endDate: String,
        baseCurrency: String,
        currencies: String
    ): Response<FluctuationModel> =
        services.getFluctuationData(
            apikey = apiKey,
            base = baseCurrency,
            startDate = startDate,
            endDate = endDate,
            symbols = currencies
        )

    override suspend fun getHistorical(
        apiKey: String,
        baseCurrency: String,
        currencies: String,
        date: String
    ): Response<HistoricalRatesModel> =
        services.getHistoricalData(
            apiKey = apiKey,
            baseCurrency = baseCurrency,
            symbols = currencies,
            date = date
        )
}