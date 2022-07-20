package com.example.currencyexchange.Repository

import androidx.annotation.WorkerThread
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import kotlinx.coroutines.flow.Flow

class CurrencyDatabaseRepository(private val currencyDAO: CurrencyDAO) {

    val allCurrencyNamesModel: Flow<List<CurrencyDatabaseModel>> = currencyDAO.getAllCurrencies()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNewCurrency(currencyDatabaseModel: CurrencyDatabaseModel){
        currencyDAO.insertNewCurrency(currencyDatabaseModel)
    }
}