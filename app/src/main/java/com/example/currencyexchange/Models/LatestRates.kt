package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName
import java.util.SortedMap

data class LatestRates(
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("rates")
    val latestRates: SortedMap<String, Double>)

