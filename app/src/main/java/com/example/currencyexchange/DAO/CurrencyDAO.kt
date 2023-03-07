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

    /** Insert new currency to the database. In case, where currency already exists in database, just ignore it*/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCurrency(databaseModel: CurrencyNamesModel)

    //  Update base currency
    @Update
    suspend fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel)

    @Query("SELECT currency_name FROM currency_names ORDER BY currency_name ASC ")
    fun getCurrencies(): Flow<List<CurrencyNamesModel>>

//    TODO CHANGE
    //  Get base currency from the database
    @Query("SELECT * from base_currency")
    fun getBaseCurrency(): Flow<BaseCurrencyModel>
}