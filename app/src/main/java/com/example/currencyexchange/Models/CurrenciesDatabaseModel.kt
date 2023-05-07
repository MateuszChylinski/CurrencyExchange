package com.example.currencyexchange.Models

import androidx.room.*

@Entity(tableName = "currency_main")
data class CurrenciesDatabaseMain(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("base_currency")
    val baseCurrency: String)

@Entity(tableName = "currency_detailed")
data class CurrenciesDatabaseDetailed(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("rates_date")
    val ratesDate: String?,
    @ColumnInfo(name = "currency_data")
    val currencyData: Map<String, Double> = mapOf())

