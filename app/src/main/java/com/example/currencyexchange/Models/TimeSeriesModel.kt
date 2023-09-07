package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class TimeSeriesModel(
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("rates")
    val timeSeriesRates: Map<String, Map<String, Double>>

)
