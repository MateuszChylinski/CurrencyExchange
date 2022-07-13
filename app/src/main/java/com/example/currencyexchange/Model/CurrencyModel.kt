package com.example.currencyexchange.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CurrencyModel(
    @SerializedName("success")
    val success: String?,
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("base")
    val base: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("result")
    val result: Double?,
    @SerializedName("rates")
    val rates: HashMap<String, Double>
)

data class Query(
    @SerializedName("from")
    val from: String?,
    @SerializedName("to")
    val to: String?,
    @SerializedName("amount")
    val amount: Double?
)

data class Info(
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("rate")
    val currencyRate: Double?
)


