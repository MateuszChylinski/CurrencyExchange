package com.example.currencyexchange.Repository.Implementation

import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Models.TimeSeriesModel
import com.example.currencyexchange.Repository.Interfaces.RetrofitRepository
import retrofit2.Response
import javax.inject.Inject

class RetrofitRepositoryImplementation @Inject constructor(private val services: ApiServices) :
    RetrofitRepository {

    override suspend fun getLatestRates(
        apiKey: String,
        baseCurrency: String
    ): Response<LatestRates> =
        services.getLatestRates(apikey = apiKey, base = baseCurrency)

    override suspend fun convertCurrency(
        apiKey: String,
        baseCurrency: String,
        wantedCurrency: String,
        amount: String
    ): Response<ConversionModel> =
        services.convertCurrency(
            apiKey = apiKey,
            from = baseCurrency,
            to = wantedCurrency,
            amount = amount
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

    override suspend fun getTimeSeriesData(
        apiKey: String,
        baseCurrency: String,
        currencies: String,
        startDate: String,
        endDate: String
    ): Response<TimeSeriesModel> =
        services.getTimeSeries(
            apikey = apiKey,
            base = baseCurrency,
            symbols = currencies,
            startDate = startDate,
            endDate = endDate
        )
}