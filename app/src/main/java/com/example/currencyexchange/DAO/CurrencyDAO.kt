package com.example.currencyexchange.DAO

import androidx.room.*
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {

    /**  Insert base curr whenever database is created. Default currency is set to EUR  */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultCurrency(databaseModel: BaseCurrencyModel)

    /** Insert new currency to the database. In case, where currency already exists in database, just ignore it */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCurrency(databaseModel: List<CurrencyNamesModel>)

    /** Update base currency */
    @Update
    suspend fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel)

    /** Get list with all of the available currencies from the database */
    @Query("SELECT currency_name FROM currency_names ORDER BY currency_name ASC ")
    fun getCurrencies(): Flow<List<CurrencyNamesModel>>

    /** Get base currency from the database*/
    @Query("SELECT * from base_currency")
    fun getBaseCurrency(): Flow<BaseCurrencyModel>
}