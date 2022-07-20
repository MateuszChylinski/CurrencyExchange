package com.example.currencyexchange.Application

import android.app.Application
import com.example.currencyexchange.Database.CurrencyDatabase
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CurrencyApplication : Application() {
    val coroutine = CoroutineScope(SupervisorJob())
    val database by lazy {CurrencyDatabase.getDatabase(this, coroutine)}
    val repository by lazy {CurrencyDatabaseRepository(database.getDAO())}
}