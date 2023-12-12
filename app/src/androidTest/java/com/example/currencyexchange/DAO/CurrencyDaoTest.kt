package com.example.currencyexchange.DAO

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.currencyexchange.Database.CurrencyDatabase
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString

@RunWith(AndroidJUnit4::class)
class CurrencyDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: CurrencyDatabase
    private lateinit var dao: CurrencyDAO

    private lateinit var currenciesToInsert: CurrenciesDatabaseDetailed
    private lateinit var currenciesToUpdate: CurrenciesDatabaseDetailed
    private lateinit var defaultCurrency: CurrenciesDatabaseMain
    private lateinit var baseCurrencyToUpdate: CurrenciesDatabaseMain

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.getDAO()

        currenciesToInsert = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = anyString(),
            currencyData = mapOf(anyString() to anyDouble())
        )
        currenciesToUpdate = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = "unknown currency",
            ratesDate = anyString(),
            currencyData = mapOf(anyString() to anyDouble())
        )
        defaultCurrency = CurrenciesDatabaseMain(
            id = 1,
            baseCurrency = anyString()
        )
        baseCurrencyToUpdate = CurrenciesDatabaseMain(
            id = 1,
            baseCurrency = "unknown currency"
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertDefaultBaseCurrencyReturnSuccess() {
        runTest {
            dao.insertDefaultCurrency(defaultCurrency)
            val baseCurrency = dao.getBaseCurrency().asLiveData().getOrAwaitValue()
            assertThat(baseCurrency).isEqualTo(defaultCurrency)
        }
    }

    @Test
    fun insertNewCurrenciesReturnSuccess() {
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            val currencies = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(currencies[0]).isEqualTo(currenciesToInsert)
        }
    }

    @Test
    fun updateCurrenciesDataReturnSuccess() {
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            dao.updateCurrenciesData(currenciesToUpdate)
            val getUpdatedCurrency = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedCurrency[0]).isEqualTo(currenciesToUpdate)
        }
    }

    @Test
    fun getBaseCurrencyReturnSuccess() {
        runTest {
            dao.insertDefaultCurrency(defaultCurrency)
            val baseCurrency = dao.getBaseCurrency().asLiveData().getOrAwaitValue()
            assertThat(baseCurrency).isNotNull()
        }
    }

    @Test
    fun getCurrencyDataReturnSuccess() {
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            val currencies = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(currencies).isNotNull()
        }
    }

    @Test
    fun updateBaseCurrencyReturnSuccess() {
        runTest {
            dao.insertDefaultCurrency(defaultCurrency)
            dao.updateBaseCurrency(baseCurrencyToUpdate.baseCurrency)

            val getUpdatedBaseCurrency = dao.getBaseCurrency().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedBaseCurrency).isEqualTo(baseCurrencyToUpdate)
        }
    }

    @Test
    fun checkIfDbIsInitReturnSuccess() {
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            val isInit = dao.checkIfInit().asLiveData().getOrAwaitValue()
            assertThat(isInit).isFalse()
        }
    }


    //false section
    @Test
    fun insertDefaultBaseCurrencyWithoutCurrencyNameReturnError() {
        val modelToFail = CurrenciesDatabaseMain(
            id = 1,
            baseCurrency = ""
        )
        runTest {
            dao.insertDefaultCurrency(modelToFail)
            val getBaseCurrency = dao.getBaseCurrency().asLiveData().getOrAwaitValue()
            assertThat(getBaseCurrency.baseCurrency).isEmpty()
        }
    }

    @Test
    fun insertNewCurrenciesWithEmptyBaseCurrencyReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = "",
            ratesDate = anyString(),
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            dao.insertCurrencyData(modelToFail)
            val currencies = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(currencies[0].baseCurrency).isEmpty()
        }
    }

    @Test
    fun insertNewCurrenciesWithEmptyDateReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = "",
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            dao.insertCurrencyData(modelToFail)
            val currencies = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(currencies[0].ratesDate).isEmpty()
        }
    }

    @Test
    fun insertNewCurrenciesWithEmptyCurrencyDataReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = anyString(),
            currencyData = mapOf()
        )
        runTest {
            dao.insertCurrencyData(modelToFail)
            val currencies = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(currencies[0].currencyData).isEmpty()
        }
    }


    @Test
    fun updateCurrenciesDataWithEmptyBaseCurrencyReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = "",
            ratesDate = anyString(),
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            dao.updateCurrenciesData(modelToFail)
            val getUpdatedCurrency = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedCurrency[0].baseCurrency).isEmpty()
        }
    }

    @Test
    fun updateCurrenciesDataWithEmptyDateReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = "",
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            dao.updateCurrenciesData(modelToFail)
            val getUpdatedCurrency = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedCurrency[0].ratesDate).isEmpty()
        }
    }

    @Test
    fun updateCurrenciesDataWithEmptyCurrencyDataReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = anyString(),
            currencyData = mapOf()
        )
        runTest {
            dao.insertCurrencyData(currenciesToInsert)
            dao.updateCurrenciesData(modelToFail)
            val getUpdatedCurrency = dao.getCurrencyListData().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedCurrency[0].currencyData).isEmpty()
        }
    }

    @Test
    fun updateBaseCurrencyWithEmptyBaseCurrencyReturnError() {
        val modelToFail = CurrenciesDatabaseMain(
            id = 1,
            baseCurrency = ""
        )
        runTest {
            dao.insertDefaultCurrency(defaultCurrency)
            dao.updateBaseCurrency(modelToFail.baseCurrency)

            val getUpdatedBaseCurrency = dao.getBaseCurrency().asLiveData().getOrAwaitValue()
            assertThat(getUpdatedBaseCurrency.baseCurrency).isEmpty()
        }
    }
}
