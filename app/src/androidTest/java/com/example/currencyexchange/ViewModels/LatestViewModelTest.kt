package com.example.currencyexchange.ViewModels

import android.net.ConnectivityManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Database.CurrencyDatabase
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Repository.CurrencyRepositoryImplementation
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LatestViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    private lateinit var viewModel: LatestViewModel
    private lateinit var database: CurrencyDatabase

    private lateinit var currenciesToInsert: CurrenciesDatabaseDetailed
    private lateinit var currenciesToUpdate: CurrenciesDatabaseDetailed
    private lateinit var defaultCurrency: CurrenciesDatabaseMain
    private lateinit var baseCurrencyToUpdate: CurrenciesDatabaseMain

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CurrencyDatabase::class.java
        ).allowMainThreadQueries().build()
        val dao = database.getDAO()
        val repository = CurrencyRepositoryImplementation(
            dao,
            mock(ApiServices::class.java),
            mock(ConnectivityManager::class.java)
        )
        viewModel = LatestViewModel(repository)

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
    fun testInsertCurrenciesReturnSuccess() {
        runBlocking {
            viewModel.insertCurrencies(currenciesToInsert)
            val getCurrencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(getCurrencies.data?.get(0)).isEqualTo(currenciesToInsert)
        }
    }

    @Test
    fun testUpdateCurrenciesReturnSuccess() {
        runBlocking {
            viewModel.insertCurrencies(currenciesToInsert)
            viewModel.updateCurrencies(currenciesToUpdate)
            val updatedCurrencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(updatedCurrencies.data?.get(0)).isEqualTo(currenciesToUpdate)
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
            viewModel.insertCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data?.get(0)?.baseCurrency).isEmpty()
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
            viewModel.insertCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data?.get(0)?.ratesDate).isEmpty()
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
            viewModel.insertCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data?.get(0)?.currencyData).isEmpty()
        }
    }


    @Test
    fun updateCurrenciesWithEmptyBaseCurrencyReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = "",
            ratesDate = anyString(),
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            viewModel.updateCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data).isEmpty()
//            assertThat(currencies.data?.get(0)?.baseCurrency).isEmpty()
        }
    }

    @Test
    fun updateCurrenciesWithEmptyDateReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = "",
            currencyData = mapOf(anyString() to anyDouble())
        )
        runTest {
            viewModel.updateCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data).isEmpty()
        }
    }

    @Test
    fun updateWithEmptyCurrencyDataReturnError() {
        val modelToFail = CurrenciesDatabaseDetailed(
            id = 1,
            baseCurrency = anyString(),
            ratesDate = anyString(),
            currencyData = anyMap()
        )
        runTest {
            viewModel.updateCurrencies(modelToFail)
            val currencies = viewModel.currencyDataList.asLiveData().getOrAwaitValue()
            assertThat(currencies.data).isEmpty()

//            assertThat(currencies.data?.get(0)?.currencyData).isEmpty()
        }
    }

    @Test
    fun task() {
        runBlocking {
            val modelToFail = CurrenciesDatabaseDetailed(
                id = 1,
                baseCurrency = anyString(),
                ratesDate = anyString(),
                currencyData = anyMap()
            )
            viewModel.insertCurrencies(modelToFail)
        }
    }
}

//    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
//        currencyRepository.baseCurrency
//            .map { DataWrapper.Success(it) }
//            .catch { DataWrapper.Error(it.message) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
//
//    val currencyDataList: SharedFlow<DataWrapper<List<CurrenciesDatabaseDetailed>>> =
//        currencyRepository.currencyListData
//            .map { DataWrapper.Success(it) }
//            .catch { DataWrapper.Error(it.message) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
//
//    val isDbInit: SharedFlow<DataWrapper<Boolean>> =
//        currencyRepository.isInit
//            .map { DataWrapper.Success(it) }
//            .catch { DataWrapper.Error(it) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
//
//    val internetConnection: SharedFlow<DataWrapper<NetworkStatus>> =
//        currencyRepository.observeNetworkStatus()
//            .map { DataWrapper.Success(it) }
//            .catch { DataWrapper.Error(it.message) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
//

//}