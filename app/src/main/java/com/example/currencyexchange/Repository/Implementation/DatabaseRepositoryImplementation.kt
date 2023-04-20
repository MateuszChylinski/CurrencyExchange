package com.example.currencyexchange.Repository.Implementation

import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Repository.Interfaces.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepositoryImplementation @Inject constructor(
    private val currencyDAO: CurrencyDAO
) : DatabaseRepository {

    override val baseCurrency: Flow<CurrenciesDatabaseMain>
        get() = currencyDAO.getBaseCurrency()
    override val currencyData: Flow<CurrenciesDatabaseDetailed>
        get() = currencyDAO.getCurrencyData()

    override suspend fun insertCurrencies(currency: CurrenciesDatabaseDetailed) {
        currencyDAO.insertCurrencyData(currency)
    }

    override suspend fun updateBaseCurrency(baseCurrency: CurrenciesDatabaseMain) {
        currencyDAO.updateBaseCurrency(baseCurrency.baseCurrency)
    }

    override suspend fun updateRates(currency: CurrenciesDatabaseDetailed) {
        currencyDAO.updatesCurrencyData(currency.currencyData)
    }

    override suspend fun updateRatesDate(date: String?) {
        currencyDAO.updateRatesDate(date)
    }
}