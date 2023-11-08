package com.example.currencyexchange.ViewModels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.CustomRule
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.LatestRates
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
class LatestViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val customRule = CustomRule()

    @Mock
    lateinit var repository: CurrencyRepository
    lateinit var viewModel: LatestViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        repository = mock(CurrencyRepository::class.java)
        viewModel = LatestViewModel(repository)

    }

    @Test
    fun `get latest rates return success`() {

        val model = LatestRates(
            success = true,
            timestamp = 1,
            baseCurrency = "Unknown currency",
            date = "Unknown date",
            latestRates = mapOf("Second unknown currency" to 0.0)
        )

        runTest {
            `when`(
                repository.getLatestRates(
                    anyString(), anyString()
                )
            ).thenReturn(DataWrapper.Success(model))

            //check if data was fetched
            viewModel.fetchData("unknown currency")

            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.latestRates.getOrAwaitValue()
            assertThat(response.data).isEqualTo(model)
        }
    }

    @Test
    fun `get latest rates return error`() {

        val errorModel = LatestRates(
            success = false,
            timestamp = 0,
            baseCurrency = "Error",
            date = "Error occurred",
            latestRates = mapOf("Error" to 0.0)
        )

        runTest {
            `when`(
                repository.getLatestRates(
                    anyString(), anyString()
                )
            ).thenReturn(DataWrapper.Error(errorModel, "Explanation why error occurred"))

            //check if data was fetched
            viewModel.fetchData("unknown currency")

            customRule.testDispatcher.scheduler.advanceUntilIdle()
            val response = viewModel.latestRates.getOrAwaitValue()
            assertThat(response.message).isNotNull()
        }
    }
}