package com.example.currencyexchange.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.ConversionInfo
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.ConversionQuery
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
class ConversionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()


    @Mock
    private lateinit var repository: CurrencyRepository
    private lateinit var viewModel: ConversionViewModel


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = mock(CurrencyRepository::class.java)
        viewModel = ConversionViewModel(repository)
    }

    @Test
    fun `exchange currency return success`() {
        val conversionModel = ConversionModel(
            success = true,
            date = "unknown date",
            result = 0.0,
            query = ConversionQuery(
                from = "unknown currency",
                to = "unknown currency",
                amount = 0
            ),
            info = ConversionInfo(
                timestamp = 0,
                rate = 0.0
            )
        )
        runTest {
            `when`(
                repository.convertCurrency(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Success(conversionModel))

            //check data
            viewModel.exchangeCurrency(
                baseCurrency = "unknown currency",
                selectedCurrency = "second unknown currency",
                amount = "0"
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.exchangeResult.getOrAwaitValue()
            assertThat(response?.data).isEqualTo(conversionModel)
        }
    }

    @Test
    fun `exchange currency return error`() {
        runTest {
            `when`(
                repository.convertCurrency(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                )
            ).thenReturn(DataWrapper.Error(null, "Error occurred"))

            //check data
            viewModel.exchangeCurrency(
                baseCurrency = "unknown currency",
                selectedCurrency = "second unknown currency",
                amount = "0"
            )
            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.exchangeResult.getOrAwaitValue()
            assertThat(response?.message).isNotNull()
        }
    }
}