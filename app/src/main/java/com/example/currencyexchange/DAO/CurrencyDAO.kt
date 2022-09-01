package com.example.currencyexchange.DAO

import androidx.room.*
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultCurrency(databaseModel: BaseCurrencyModel)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCurrency(databaseModel: CurrencyNamesModel)

    @Update
    fun updateBaseCurrency(baseCurrencyModel: BaseCurrencyModel)

    @Query("SELECT base_curr FROM base_currency")
    fun getBaseCurrency(): Flow<BaseCurrencyModel>
    @Query("SELECT currency_name FROM currency_names ORDER BY currency_name")
    fun getAllCurrencies(): Flow<List<CurrencyNamesModel>>
}