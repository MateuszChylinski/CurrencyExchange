package com.example.currencyexchange.API

import com.example.currencyexchange.Models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Query
import java.util.SortedMap

interface ApiServices {
    @GET("/fixer/latest")
    suspend fun getLatestRates(
        @Query("base") base: String,
        @Query("apikey") apikey: String
    ): Response<LatestRates>

    @GET("/fixer/convert")
    suspend fun convertCurrency(
        @Query("from") to: String,
        @Query("to") from: String,
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
        @Path("date")date: String,
        @Query("symbols") symbols: String,
        @Query("base") baseCurrency: String,
        @Query("apikey") apiKey: String
    ): Response<HistoricalRatesModel>

    companion object {
        private const val url = "https://api.apilayer.com/"

        var apiServices: ApiServices? = null
        fun getInstance(): ApiServices {
            if (apiServices == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiServices = retrofit.create(ApiServices::class.java)
            }
            return apiServices!!
        }
    }
}