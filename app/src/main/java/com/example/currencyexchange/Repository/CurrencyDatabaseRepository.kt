package com.example.currencyexchange.Repository

import androidx.annotation.WorkerThread
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.flow.Flow

class CurrencyDatabaseRepository constructor(private val currencyDAO: CurrencyDAO) {

    //  Insert new currency into the database
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNewCurrency(currencyNamesModel: CurrencyNamesModel) {
        currencyDAO.insertNewCurrency(currencyNamesModel)
    }

    //  Update base currency
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel) {
        currencyDAO.updateBaseCurrency(baseCurrencyModel)
    }

    //  Get base currency from database
    val baseCurrency: Flow<String> = currencyDAO.getBaseCurrency()

    //  Get all currencies from the database
    val allCurrencies: Flow<List<CurrencyNamesModel>> = currencyDAO.getCurrencies()

}