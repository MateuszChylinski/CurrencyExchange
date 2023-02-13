package com.example.currencyexchange.Models

import com.google.gson.annotations.SerializedName

data class ConversionModel(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("date")
    val date: String,
    @SerializedName("result")
    val result: Double,
    @SerializedName("query")
    val query: ConversionQuery,
    @SerializedName("info")
    val info: ConversionInfo
)

data class ConversionQuery(
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("amount")
    val amount: String
)

data class ConversionInfo(
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("rate")
    val rate: Double
)