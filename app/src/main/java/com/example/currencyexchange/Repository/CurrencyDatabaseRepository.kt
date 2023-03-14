package com.example.currencyexchange.Repository

import android.provider.ContactsContract.Data
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Database.CurrencyDatabase
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import java.io.IOException
import java.util.Date

class CurrencyDatabaseRepository constructor(private val currencyDAO: CurrencyDAO, scope: CoroutineScope) {

    val baseCurrency: Flow<CurrenciesDatabaseMain> = currencyDAO.getBaseCurrency()




//        suspend fun getLatestRates(
//        baseCurrency: String,
//        apiKey: String
//    ): ApiResult<LatestRates> {
//        return try {
//            val response = apiServices.getLatestRates(baseCurrency, apiKey)
//            ApiResult.Success(data = response.body()!!)
//        } catch (exception: IOException) {
//            ApiResult.Error(error = exception.message)
//        }
//    }

//

    suspend fun addCurrency(currenciesDatabaseDetailed: CurrenciesDatabaseDetailed){
        currencyDAO.addCurrency(currenciesDatabaseDetailed)
    }

    suspend fun updateRatesDate(date: Long){
        currencyDAO.updateRatesDate(date)
    }

//    val allCurrencies: Flow<List<CurrenciesDatabaseModel>> =
//        currencyDAO.getCurrencies().shareIn(
//            scope, replay = 1, started = SharingStarted.WhileSubscribed()
//        )
//
//    suspend fun addCurrency(currenciesDatabaseModel: List<CurrenciesDatabaseModel>) {
//        currencyDAO.insertNewCurrency(currenciesDatabaseModel)
//    }
//
//    suspend fun addDefaultBase(currenciesDatabaseModel: CurrenciesDatabaseModel) {
//        currencyDAO.insertDefaultCurrency(currenciesDatabaseModel)
//    }
}