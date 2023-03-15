package com.example.currencyexchange.Repository

import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class CurrencyDatabaseRepository constructor(
    private val currencyDAO: CurrencyDAO,
    scope: CoroutineScope
) {

    /** Insert map with currency names, and their rates */
    suspend fun insertCurrencyData(currenciesDatabaseDetailed: CurrenciesDatabaseDetailed) {
        currencyDAO.insertCurrencyData(currenciesDatabaseDetailed)
    }


    /** Get base currency   */
    val baseCurrency: Flow<CurrenciesDatabaseMain> = currencyDAO.getBaseCurrency()

    /** Get data about currencies (names, and their rates) to make operations on them, when there's no internet connection available    */
    val currencyData: Flow<CurrenciesDatabaseDetailed> = currencyDAO.getCurrencyData()


    /** Update currency rates, to let user know, from when they are  */
    suspend fun updateRatesDate(date: String?) {
        currencyDAO.updateRatesDate(date) }

    /** Update base currency    */
    suspend fun updateBaseCurrency(currency: String?) {
        currencyDAO.updateBaseCurrency(currency) }
}