package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.HashMap

data class HistoricalRatesModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("historical")
    val historical: Boolean,
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
)
