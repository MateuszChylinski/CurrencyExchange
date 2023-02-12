package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class FluctuationModel(
    @SerializedName("success")
    val success: String,
    @SerializedName("fluctuation")
    val fluctuation: String,
    @SerializedName("start_date")
    val start_date: String,
    @SerializedName("end_date")
    val end_date: String,
    @SerializedName("base")
    val base: String,
    @SerializedName("rates")
    val rates: Map<String, FluctuationRates>
)

data class FluctuationRates(
    @SerializedName("start_rate")
    val start_rate: Double,
    @SerializedName("end_rate")
    val end_rate: Double,
    @SerializedName("change")
    val change: Double,
    @SerializedName("change_pct")
    val change_pct: Double
)
