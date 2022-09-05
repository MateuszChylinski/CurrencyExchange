package com.example.currencyexchange.DAO

import androidx.room.*
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {

    //  Insert default currency, for default, it'll be set as euro. If database will contain an "EUR" value, just replace it with second parameter of the data class object ("isBase") = true
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultCurrency(databaseModel: BaseCurrencyModel)

    // Insert new currency into database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCurrency(databaseModel: CurrencyNamesModel)

    //  Get all currency names, and boolean "isBase" to check whenever the value is set as base currency or not (1 = yes / 0 = no)
    @Query("SELECT currency_name FROM currency_names ORDER BY currency_name ASC ")
    fun getCurrencies(): Flow<List<CurrencyNamesModel>>

    //  Get base currency from the database
    @Query("SELECT base FROM base_currency")
    fun getBaseCurrency(): Flow<String>

    //  Update base currency
    @Update
    suspend fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel)
}