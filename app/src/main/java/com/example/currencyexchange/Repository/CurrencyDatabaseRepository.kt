package com.example.currencyexchange.Repository

import androidx.annotation.WorkerThread
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.flow.Flow

class CurrencyDatabaseRepository(private val currencyDAO: CurrencyDAO) {


//  Base currency
    val baseCurrency: Flow<BaseCurrencyModel> = currencyDAO.getBaseCurrency()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertNewCurrency(currencyNamesModel: CurrencyNamesModel){
        currencyDAO.insertNewCurrency(currencyNamesModel)
    }

//  All currencies
    val allCurrencyNamesModel: Flow<List<CurrencyNamesModel>> = currencyDAO.getAllCurrencies()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel){
        currencyDAO.updateBaseCurrency(baseCurrencyModel)
    }
}