package com.example.currencyexchange.DAO

import androidx.room.*
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {

    /**  Insert base curr whenever database is created. Default currency is set to EUR  */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultCurrency(databaseModel: CurrenciesDatabaseMain)

    /** Insert map with currency names, and their rates into database   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencyData(detailed: CurrenciesDatabaseDetailed)

    @Query("UPDATE currency_detailed SET for_base = :baseW, rates_date = :dateW, currency_data =:data WHERE id = :id")
    suspend fun updatesCurrencyData(id: Int, baseW: String, dateW: String, data: Map<String, Double>)

    /** Get id, base currency, and date of rates from database  */
    @Query("SELECT * FROM currency_main")
    fun getBaseCurrency(): Flow<CurrenciesDatabaseMain>

    /** Get data about currencies (names, and their rates) to make operations on them, when there's no internet connection available    */
    @Query("SELECT * FROM currency_detailed")
    fun getCurrencyData(): Flow<CurrenciesDatabaseDetailed>

    /** Get list of 'CurrenciesDatabaseDetailed' objects. It'll be used in offline 'mode' */
    @Query("SELECT * FROM currency_detailed")
    fun getCurrencyListData(): Flow<List<CurrenciesDatabaseDetailed>>

    /** Update base currency */
    @Query("UPDATE currency_main SET base_currency = :currency WHERE id = 1")
    suspend fun updateBaseCurrency(currency: String)

    /** Check if database contains any data, and return Boolean */
    @Query("SELECT (SELECT COUNT(*) FROM currency_detailed) == 0")
    fun checkIfInit(): Flow<Boolean>
}