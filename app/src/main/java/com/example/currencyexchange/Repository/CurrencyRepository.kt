package com.example.currencyexchange.Repository

import com.example.currencyexchange.API.ApiServices


class CurrencyRepository constructor(private val apiServices: ApiServices) {
//  TODO In order to make a proper call, insert the api key
    fun fetchLatestRates() = apiServices.getRatesData("EUR", "API_KEY")
}