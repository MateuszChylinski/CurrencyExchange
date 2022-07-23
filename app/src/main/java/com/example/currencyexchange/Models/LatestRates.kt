package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

//This class exists, because I can't call "rates" object more than once in root Model class.
data class LatestRates(
    @SerializedName("rates")
    val latestRates: HashMap<String, Double>
)

