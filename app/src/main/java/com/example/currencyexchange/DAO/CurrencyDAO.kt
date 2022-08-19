package com.example.currencyexchange.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDAO {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCurrency(databaseModel: CurrencyDatabaseModel)

    @Query("SELECT currency_name FROM currencies ORDER BY currency_name")
    fun getAllCurrencies(): Flow<List<CurrencyDatabaseModel>>
}