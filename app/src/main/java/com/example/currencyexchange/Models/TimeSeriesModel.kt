package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class TimeSeriesModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("timeseries")
    val timeseries: Boolean,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("base")
    val baseCurrency: String,
    @SerializedName("rates")
    val timeSeriesRates: Map<String, Map<String, Double>>
)
