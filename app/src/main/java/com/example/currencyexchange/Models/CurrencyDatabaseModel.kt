package com.example.currencyexchange.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currencies")
data class CurrencyDatabaseModel(
    @PrimaryKey @ColumnInfo(name = "currency_name") val currency: String
)