package com.example.currencyexchange.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.FluctuationRates
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FluctuationViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    @Mock
    lateinit var repository: CurrencyRepository
    lateinit var viewModel: FluctuationViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = mock(CurrencyRepository::class.java)
        viewModel = FluctuationViewModel(repository)
    }

    @Test
    fun `get fluctuation data return success`() {
        val model = FluctuationModel(
            message = "unknown message",
            success = true,
            fluctuation = "unknown fluctuation",
            start_date = "unknown start date",
            end_date = "Unknown end date",
            base = "unknown base currency",
            rates = mapOf(
                "Unknown currency" to FluctuationRates(
                    start_rate = 0.0,
                    end_rate = 0.0,
                    change = 0.0,
                    change_pct = 0.0
                )
            )
        )

        runTest {
            `when`(
                repository.getFluctuation(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Success(model))

            //check data
            viewModel.fetchFluctuation(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                startDate = "unknown start date",
                endDate = "unknown end date",
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.fluctuationResponse.getOrAwaitValue()
            assertThat(response?.data).isEqualTo(model)
        }
    }

    @Test
    fun `get fluctuation data return error`() {
        runTest {
            `when`(
                repository.getFluctuation(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Error(null, "Unknown error occurred"))

            //check data
            viewModel.fetchFluctuation(
                baseCurrency = "unknown base currency",
                selectedCurrencies = "unknown selected currencies",
                startDate = "unknown start date",
                endDate = "unknown end date",
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.fluctuationResponse.getOrAwaitValue()
            assertThat(response?.message).isNotNull()
        }
    }
}