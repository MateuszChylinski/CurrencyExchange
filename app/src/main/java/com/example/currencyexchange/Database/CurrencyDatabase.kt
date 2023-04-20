package com.example.currencyexchange.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.DataWrapper.CustomTypeConverters
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import java.util.*

@Database(
    entities = [CurrenciesDatabaseMain::class, CurrenciesDatabaseDetailed::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CustomTypeConverters::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun getDAO(): CurrencyDAO
}
