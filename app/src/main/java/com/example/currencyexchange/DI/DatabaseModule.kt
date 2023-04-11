package com.example.currencyexchange.DI

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.currencyexchange.Database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun createDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context, CurrencyDatabase::class.java, "currency_database")
        .build()

    @Provides
    @Singleton
    fun provideDao(db: CurrencyDatabase) = db.getDAO()
}