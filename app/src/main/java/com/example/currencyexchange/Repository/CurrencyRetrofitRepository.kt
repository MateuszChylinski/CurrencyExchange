package com.example.currencyexchange.Repository

import com.example.currencyexchange.API.ApiResult
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Models.LatestRates
import java.io.IOException

class CurrencyRetrofitRepository constructor(private val apiServices: ApiServices) {

    suspend fun getLatestRates(
        baseCurrency: String,
        apiKey: String
    ): ApiResult<LatestRates> {
        return try {
            val response = apiServices.getLatestRates(baseCurrency, apiKey)
            ApiResult.Success(data = response.body()!!)
        } catch (exception: IOException) {
            ApiResult.Error(error = exception.message)
        }
    }
}

