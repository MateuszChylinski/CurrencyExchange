package com.example.currencyexchange.API

import com.example.currencyexchange.Models.*
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Query

interface ApiServices {
    @GET("/fixer/latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("apikey") apikey: String
    ): Response<LatestRates>

    @GET("/fixer/convert")
    suspend fun convertCurrency(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: String,
        @Query("apikey") apiKey: String
    ): Response<ConversionModel>

    @GET("/fixer/fluctuation")
    suspend fun getFluctuationData(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String,
        @Query("apikey") apikey: String
    ): Response<FluctuationModel>

    @GET("/fixer/{date}")
    suspend fun getHistoricalData(
        @Path("date") date: String,
        @Query("symbols") symbols: String,
        @Query("base") baseCurrency: String,
        @Query("apikey") apiKey: String
    ): Response<HistoricalRatesModel>

    @GET("/fixer/timeseries")
    suspend fun getTimeSeries(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String,
        @Query("apikey") apikey: String
    ): Response<TimeSeriesModel>
}