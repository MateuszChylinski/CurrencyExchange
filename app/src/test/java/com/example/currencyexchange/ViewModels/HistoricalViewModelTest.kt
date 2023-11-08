package com.example.currencyexchange.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HistoricalViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    @Mock
    lateinit var repository: CurrencyRepository
    lateinit var viewModel: HistoricalViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = Mockito.mock(CurrencyRepository::class.java)
        viewModel = HistoricalViewModel(repository)
    }

    @Test
    fun `get historical data return success`() {
        val model = HistoricalRatesModel(
            success = true,
            timestamp = 0,
            base = "unknown base currency",
            rates = mapOf("unknown currency" to 0.0),
            date = "unknown date",
            historical = true
        )

        runTest {
            Mockito.`when`(
                repository.getHistorical(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Success(model))

            //check data
            viewModel.fetchHistoricalData(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                date = "unknown date"
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.historicalData.getOrAwaitValue()
            Truth.assertThat(response?.data).isEqualTo(model)
        }
    }

    @Test
    fun `get historical data return error`() {
        runTest {
            Mockito.`when`(
                repository.getHistorical(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Error(null, "Unknown error occurred"))

            //check data
            viewModel.fetchHistoricalData(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                date = "unknown date",
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.historicalData.getOrAwaitValue()
            Truth.assertThat(response?.message).isNotNull()
        }
    }
}

