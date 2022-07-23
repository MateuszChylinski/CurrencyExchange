package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class CurrencyModel(
    @SerializedName("success")
    val success: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("result")
    val result: Double,
    @SerializedName("rates")
    val rates: Map<String, Rates>,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDAte: String
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

data class Rates(
    @SerializedName("start_rate")
    val startRate: Double,
    @SerializedName("end_rate")
    val endRate: Double,
    @SerializedName("change")
    val change: Double,
    @SerializedName("change_pct")
    val change_pct: Double

)