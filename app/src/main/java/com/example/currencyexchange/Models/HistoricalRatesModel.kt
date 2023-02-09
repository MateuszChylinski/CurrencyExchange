package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.HashMap

data class HistoricalRatesModel(
    @SerializedName("success")
    val success: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("historical")
    val historical: String,
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: HashMap<String, Double>
)
