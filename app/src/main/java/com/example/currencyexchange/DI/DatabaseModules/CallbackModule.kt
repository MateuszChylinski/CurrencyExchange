package com.example.currencyexchange.DI.DatabaseModules

import android.annotation.SuppressLint
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class CallbackModule @Inject constructor(
    private val provider: Provider<CurrencyDAO>): RoomDatabase.Callback() {
    private val scope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch {
            insertDefaultCurrency()
        }
    }
    @SuppressLint("SimpleDateFormat")
    private suspend fun insertDefaultCurrency(){
        val defaultCurrency = CurrenciesDatabaseMain(0, "EUR", SimpleDateFormat("yyy-MM-dd").format(Calendar.getInstance().time))
        provider.get().insertDefaultCurrency(defaultCurrency)
    }
}