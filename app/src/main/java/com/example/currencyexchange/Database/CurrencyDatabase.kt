package com.example.currencyexchange.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [CurrencyNamesModel::class, BaseCurrencyModel::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyDatabase() : RoomDatabase() {

    abstract fun getDAO(): CurrencyDAO

    companion object {
        @Volatile
        private var INSTANCE: CurrencyDatabase? = null
        private val DB_NAME = "currency_database"

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
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE.let { db ->
                scope.launch {
                    val dao = db?.getDAO()
                    val baseCurr = BaseCurrencyModel(1, "EUR")
                    dao?.insertDefaultCurrency(baseCurr)
                }
            }
        }
    }
}

