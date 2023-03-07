package com.example.currencyexchange.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_names")
data class CurrencyNamesModel(
    @PrimaryKey
    @ColumnInfo(name = "currency_name")
    val currency: String,
)

@Entity(tableName = "base_currency")
data class BaseCurrencyModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "base") val baseCurr: String
)
