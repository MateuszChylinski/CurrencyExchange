package com.example.currencyexchange.DI.RetrofitModules

import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Repository.Implementation.RetrofitRepositoryImplementation
import com.example.currencyexchange.Repository.Interfaces.RetrofitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private const val url = "https://api.apilayer.com/"

    // Because the data from api does not comes immediately, make an okHttpClient, to extend waiting time for the data.
    @Singleton
    @Provides
    fun provideHttpClient() = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiServices(retrofit: Retrofit) = retrofit.create(ApiServices::class.java)

    @Singleton
    @Provides
    fun provideServiceHelper(servicesHelper: RetrofitRepositoryImplementation): RetrofitRepository = servicesHelper
}