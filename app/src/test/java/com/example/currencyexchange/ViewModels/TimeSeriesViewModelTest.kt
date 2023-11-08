package com.example.currencyexchange.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.TimeSeriesModel
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
class TimeSeriesViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    @Mock
    lateinit var repository: CurrencyRepository
    lateinit var viewModel: TimeSeriesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = Mockito.mock(CurrencyRepository::class.java)
        viewModel = TimeSeriesViewModel(repository)
    }

    @Test
    fun `get time series data return success`() {
        val model = TimeSeriesModel(
            success = true,
            timeseries = true,
            startDate = "unknown start date",
            endDate = "unknown end date",
            baseCurrency = "unknown base currency",
            timeSeriesRates = mapOf(
                "unknown date" to mapOf(
                    "unknown currency" to 0.0
                )
            )
        )
        runTest {
            Mockito.`when`(
                repository.getTimeSeriesData(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString()
                )
            ).thenReturn(DataWrapper.Success(model))

            //check data
            viewModel.fetchTimeSeriesData(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                startDate = "unknown start date",
                endDate = "unknown end date",
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.timeSeriesData.getOrAwaitValue()
            Truth.assertThat(response?.data).isEqualTo(model)
        }
    }

    @Test
    fun `get time series data return error`() {
        runTest {
            Mockito.`when`(
                repository.getTimeSeriesData(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString()
                )
            ).thenReturn(DataWrapper.Error(null, "Unknown error occurred"))

            //check data
            viewModel.fetchTimeSeriesData(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                startDate = "unknown start date",
                endDate = "unknown end date",
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.timeSeriesData.getOrAwaitValue()
            Truth.assertThat(response?.message).isNotNull()
        }
    }
}
