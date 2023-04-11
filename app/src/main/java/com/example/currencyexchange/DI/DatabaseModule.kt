package com.example.currencyexchange.DI

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun createDatabase(
        @ApplicationContext context: Context,
        provider: Provider<CurrencyDAO>
    ) = Room.databaseBuilder(
        context, CurrencyDatabase::class.java, "currency_database")
        .addCallback(CallbackModule(provider))
        .build()

    @Provides
    @Singleton
    fun provideDao(db: CurrencyDatabase) = db.getDAO()
}