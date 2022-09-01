package com.example.currencyexchange.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_currency")
data class BaseCurrencyModel(
    @PrimaryKey
    @ColumnInfo(name = "base_curr")
    val baseCurrency: String


) {
    override fun toString(): String {
        return this.baseCurrency
    }
}