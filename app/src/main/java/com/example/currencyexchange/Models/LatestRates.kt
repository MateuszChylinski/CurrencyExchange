package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class LatestRates(
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val latestRates: Map<String, Double>)

