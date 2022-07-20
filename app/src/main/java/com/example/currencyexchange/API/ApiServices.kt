package com.example.currencyexchange.API

import com.example.currencyexchange.Models.CurrencyModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("/fixer/latest")
    fun getRatesData(
        @Query("base") base: String,
        @Query("apikey") apikey: String
    ): Call<CurrencyModel>

    @GET("/fixer/convert")
    fun convertCurrency(
        @Query("to") to: String,
        @Query("from") from: String,
        @Query("amount") amount: Double?,
        @Query("apikey") apiKey: String
    ): Call<CurrencyModel>


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