package com.example.currencyexchange.Repository

import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class CurrencyDatabaseRepository constructor(val currencyDAO: CurrencyDAO, scope: CoroutineScope) {

    val baseCurrency: Flow<BaseCurrencyModel> =
        currencyDAO.getBaseCurrency().shareIn(
           scope, replay = 1, started = SharingStarted.WhileSubscribed())

    val allCurrencies: Flow<List<CurrencyNamesModel>> =
        currencyDAO.getCurrencies().shareIn(
        scope, replay = 1, started = SharingStarted.WhileSubscribed()
    )

    suspend fun addCurrency(currencyNamesModel: List<CurrencyNamesModel>){
        currencyDAO.insertNewCurrency(currencyNamesModel)
    }
}