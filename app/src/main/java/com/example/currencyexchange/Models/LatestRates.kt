package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class LatestRates(
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("rates")
    val latestRates: HashMap<String, Double>,

)

