package com.example.currencyexchange.DI.SingletonCustomModules

import com.example.currencyexchange.Singletons.DataModifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CustomSingletonModule {

    @Singleton
    @Provides
    fun provideCustomSingleton(): DataModifier{
        return DataModifier
    }
}