package com.example.currencyexchange.Repository.Interfaces

import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    val baseCurrency: Flow<CurrenciesDatabaseMain>
    val currencyData: Flow<CurrenciesDatabaseDetailed>

    suspend fun insertCurrencies(currency: CurrenciesDatabaseDetailed)
    suspend fun updateBaseCurrency(baseCurrency: CurrenciesDatabaseMain)
    suspend fun updateRates(currency: CurrenciesDatabaseDetailed)
    suspend fun updateRatesDate(date: String?)

    //TODO TEST
    val isInit: Flow<Boolean>
}
