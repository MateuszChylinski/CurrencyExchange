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

    /** Update currency rates, to let user know, from when they are */
    @Query("UPDATE currency_main SET rates_date = :date WHERE id = 1")
    suspend fun updateRatesDate(date: String?)
    @Query("UPDATE currency_detailed SET currency_data =:data WHERE id = 1")
    suspend fun updatesCurrencyData(data: Map<String, Double>)

    /*
    @Entity(tableName = "currency_detailed")
    data class CurrenciesDatabaseDetailed(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        @ColumnInfo(name = "currency_data")
        val currencyData: Map<String, Double> = mapOf())

     */

    /** Get id, base currency, and date of rates from database  */
    @Query("SELECT * FROM currency_main")
    fun getBaseCurrency(): Flow<CurrenciesDatabaseMain>

    /** Get data about currencies (names, and their rates) to make operations on them, when there's no internet connection available    */
    @Query("SELECT * FROM currency_detailed")
    fun getCurrencyData(): Flow<CurrenciesDatabaseDetailed>

    /** Update base currency */
    @Query("UPDATE currency_main SET base_currency = :currency WHERE id = 1")
    suspend fun updateBaseCurrency(currency: String?)
}