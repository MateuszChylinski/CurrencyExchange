package com.example.currencyexchange.API

import com.example.currencyexchange.Models.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.http.Query

interface ApiServices {
    @GET("/fixer/latest")
    fun getRatesData(
        @Query("base") base: String,
        @Query("apikey") apikey: String
    ): Call<LatestRates>

    @GET("/fixer/convert")
    fun convertCurrency(
        @Query("from") to: String,
        @Query("to") from: String,
        @Query("amount") amount: String,
        @Query("apikey") apiKey: String
    ): Call<CurrencyModel>

    @GET("/fixer/fluctuation")
    fun getFluctuationData(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("base") base: String,
        @Query("symbols") symbols: String,
        @Query("apikey") apikey: String
    ): Call<FluctuationModel>

    @GET("/fixer/{date}")
    fun getHistoricalData(
        @Path("date")date: String,
        @Query("symbols") symbols: String,
        @Query("base") baseCurrency: String,
        @Query("apikey") apiKey: String
    ): Call<HistoricalRatesModel>

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