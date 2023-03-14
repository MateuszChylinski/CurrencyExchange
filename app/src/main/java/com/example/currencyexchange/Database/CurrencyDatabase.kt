package com.example.currencyexchange.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.DatabaseTypeConverters
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [CurrenciesDatabaseMain::class, CurrenciesDatabaseDetailed::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class CurrencyDatabase : RoomDatabase() {

    abstract fun getDAO(): CurrencyDAO

    companion object {
        @Volatile
        private var INSTANCE: CurrencyDatabase? = null
        private val DB_NAME = "currency_database"
        tr
        fun getDatabase(
            context: Context,
            coroutineScope: CoroutineScope
        ): CurrencyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyDatabase::class.java,
                    DB_NAME
                ).addCallback(CurrencyDatabaseCallback(coroutineScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class CurrencyDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                scope.launch {
                    insertDefaultCurrency(database.getDAO())
                }
            }
        }
        suspend fun insertDefaultCurrency(currencyDAO: CurrencyDAO){
            val defaultCurrency = CurrenciesDatabaseMain(0, "EUR", Calendar.getInstance().timeInMillis)
            currencyDAO.insertDefaultCurrency(defaultCurrency)
        }
    }
}

