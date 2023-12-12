package com.example.currencyexchange.Repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DAO.CurrencyDAO
import com.example.currencyexchange.Database.CurrencyDatabase
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.anyMap
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@ExperimentalCoroutinesApi
class CurrencyRepositoryImplementationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    private lateinit var database: CurrencyDatabase
    private lateinit var dao: CurrencyDAO

    @Mock
    private lateinit var repository: CurrencyRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.getDAO()
        repository = mock(CurrencyRepository::class.java)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertCurrencies() {

        runBlocking {
            val currenciesToInsert = CurrenciesDatabaseDetailed(
                id = 1,
                baseCurrency = anyString(),
                ratesDate = anyString(),
                currencyData = anyMap()
            )
            repository.insertCurrencies(currenciesToInsert)
//            val getCurrencies = repository.currencyListData.asLiveData().getOrAwaitValue()
//            assertThat(getCurrencies[0]).isEqualTo(currenciesToInsert)
        }
    }
}
//    override val baseCurrency: Flow<CurrenciesDatabaseMain>
//        get() = currencyDAO.getBaseCurrency()
//    override val currencyListData: Flow<List<CurrenciesDatabaseDetailed>>
//        get() = currencyDAO.getCurrencyListData()
//    override val isInit: Flow<Boolean>
//        get() = currencyDAO.checkIfInit()
//
//    override suspend fun insertCurrencies(currency: CurrenciesDatabaseDetailed) {
//        currencyDAO.insertCurrencyData(currency)
//    }
//
//    override suspend fun updateBaseCurrency(baseCurrency: CurrenciesDatabaseMain) {
//        currencyDAO.updateBaseCurrency(baseCurrency.baseCurrency)
//    }
//
//    override suspend fun updateRates(currency: CurrenciesDatabaseDetailed) {
//        currencyDAO.updateCurrenciesData(currency)
//    }
