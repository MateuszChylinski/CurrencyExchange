package com.example.currencyexchange.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrencyModel(
    @SerializedName("success")
    @Expose
    val success: String?,
    @SerializedName("timestamp")
    @Expose
    val timestamp: String?,
    @SerializedName("base")
    @Expose
    val base: String?,
    @SerializedName("date")
    @Expose
    val date: String?,
    @SerializedName("rates")
    @Expose
    val rates: HashMap<String, Double>,
)
