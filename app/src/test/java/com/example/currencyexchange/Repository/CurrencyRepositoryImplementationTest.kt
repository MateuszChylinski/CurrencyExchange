package com.example.currencyexchange.Repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.CustomRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
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

    private lateinit var server: MockWebServer
    private lateinit var services: ApiServices

    @Before
    fun setup() {
        //?
        MockitoAnnotations.openMocks(this)
        server = MockWebServer()
        services = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `Latest getLatestRates return 200`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{ }")
            )

            val response = services.getLatestRates("base currency", "api key")
            //?
            server.takeRequest()
            assertThat(response.code()).isEqualTo(200)
        }

    @Test
    fun `Latest getLatestRates return 404`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody("{ }")
            )

            val response = services.getLatestRates("base currency", "api key")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(404)
        }

    @Test
    fun `Latest getLatestRates missing api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(101)
                    .setBody("{\"message\":\"No API key found in request\"}")
            )

            val response = services.getLatestRates("base currency", "")
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }


    @Test
    fun `Latest getLatestRates worn api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("{\"message\":\"You have exceeded your daily\\/monthly API rate limit. Please review and upgrade your subscription plan at https:\\/\\/promptapi.com\\/subscriptions to continue.\"}")
            )

            val response = services.getLatestRates("base currency", "api key")
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }


    @Test
    fun `Conversion convertCurrency return 200`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{ }")
            )

            val response = services.convertCurrency("from", "to", "2", "api key")
            server.takeRequest()

            assertThat(response.code()).isEqualTo(200)
        }


    @Test
    fun `Conversion convertCurrency return 404`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody("{ }")
            )

            val response = services.convertCurrency("from", "to", "2", "api key")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(404)
        }

    @Test
    fun `Conversion convertCurrency missing api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(101)
                    .setBody("{\"message\":\"No API key found in request\"}")
            )

            val response = services.convertCurrency("from", "to", "2", "")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(101)

        }


    @Test
    fun `Conversion convertCurrency worn api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("{\"message\":\"You have exceeded your daily\\/monthly API rate limit. Please review and upgrade your subscription plan at https:\\/\\/promptapi.com\\/subscriptions to continue.\"}")
            )

            val response = services.convertCurrency("from", "to", "2", "api key")
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }

    @Test
    fun `Conversion convertCurrency missing from currency return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(402)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 402,\n" +
                                "        \"type\": \"invalid_from_currency\",\n" +
                                "        \"info\": \"You have entered an invalid \\\"from\\\" property. [Example: from=EUR]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.convertCurrency("", "to", "2", "api key")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(402)
        }
    }

    @Test
    fun `Conversion convertCurrency missing to currency return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(402)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 402,\n" +
                                "        \"type\": \"invalid_to_currency\",\n" +
                                "        \"info\": \"You have entered an invalid \\\"to\\\" property. [Example: to=GBP]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.convertCurrency("from", "", "2", "api key")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(402)
        }
    }

    @Test
    fun `Conversion convertCurrency missing amount currency return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(403)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 403,\n" +
                                "        \"type\": \"invalid_conversion_amount\",\n" +
                                "        \"info\": \"You have not specified an amount to be converted. [Example: amount=5]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.convertCurrency("", "to", "", "api key")
            server.takeRequest()
            assertThat(response.code()).isEqualTo(403)
        }
    }

    @Test
    fun `Fluctuation fetchFluctuation return 200`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{ }")
            )

            val response = services.getFluctuationData(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()

            assertThat(response.code()).isEqualTo(200)
        }


    @Test
    fun `Fluctuation fetchFluctuation return 404`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody("{ }")
            )

            val response = services.getFluctuationData(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(404)
        }

    @Test
    fun `Fluctuation fetchFluctuation missing api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(101)
                    .setBody("{\"message\":\"No API key found in request\"}")
            )

            val response = services.getFluctuationData(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(101)

        }


    @Test
    fun `Fluctuation fetchFluctuation worn api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("{\"message\":\"You have exceeded your daily\\/monthly API rate limit. Please review and upgrade your subscription plan at https:\\/\\/promptapi.com\\/subscriptions to continue.\"}")
            )

            val response = services.getFluctuationData(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }

    @Test
    fun `Fluctuation fetchFluctuation missing start date return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(501)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 501,\n" +
                                "        \"type\": \"no_timeframe_supplied\",\n" +
                                "        \"info\": \"You have not specified a Time-Frame. [Required format: ...&start_date=YYYY-MM-DD&end_date=YYYY-MM-DD]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.getFluctuationData(
                "",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(501)
        }
    }

    @Test
    fun `Fluctuation fetchFluctuation missing end date currency return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(501)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 501,\n" +
                                "        \"type\": \"no_timeframe_supplied\",\n" +
                                "        \"info\": \"You have not specified a Time-Frame. [Required format: ...&start_date=YYYY-MM-DD&end_date=YYYY-MM-DD]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.getFluctuationData(
                "start date",
                "",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(501)
        }
    }

    @Test
    fun `Historical fetchHistoricalData return 200`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{ }")
            )

            val response = services.getHistoricalData(
                "start date",
                "end date",
                "base currency",
                "selected currencies"
            )
            server.takeRequest()

            assertThat(response.code()).isEqualTo(200)
        }


    @Test
    fun `Historical fetchHistoricalData return 404`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody("{ }")
            )

            val response = services.getHistoricalData(
                "start date",
                "end date",
                "base currency",
                "selected currencies"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(404)
        }

    @Test
    fun `Historical fetchHistoricalData missing api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(101)
                    .setBody("{\"message\":\"No API key found in request\"}")
            )

            val response = services.getHistoricalData(
                "start date",
                "end date",
                "base currency",
                ""
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(101)

        }


    @Test
    fun `Historical fetchHistoricalData worn api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("{\"message\":\"You have exceeded your daily\\/monthly API rate limit. Please review and upgrade your subscription plan at https:\\/\\/promptapi.com\\/subscriptions to continue.\"}")
            )

            val response = services.getHistoricalData(
                "start date",
                "end date",
                "base currency",
                "selected currencies"
            )
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }

    @Test
    fun `Historical fetchHistoricalData missing date return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody(
                        "{\"message\":\"no Route matched with those values\"}"
                    )
            )

            val response = services.getHistoricalData(
                "",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.message().isNotEmpty())
        }
    }

    @Test
    fun `TimeSeries fetchTimeSeriesData return 200`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody("{ }")
            )

            val response = services.getTimeSeries(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()

            assertThat(response.code()).isEqualTo(200)
        }


    @Test
    fun `TimeSeries fetchTimeSeriesData return 404`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(404)
                    .setBody("{ }")
            )

            val response = services.getTimeSeries(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(404)
        }

    @Test
    fun `TimeSeries fetchTimeSeriesData missing api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(101)
                    .setBody("{\"message\":\"No API key found in request\"}")
            )

            val response = services.getTimeSeries(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(101)

        }


    @Test
    fun `TimeSeries fetchTimeSeriesData worn api key return error`() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setBody("{\"message\":\"You have exceeded your daily\\/monthly API rate limit. Please review and upgrade your subscription plan at https:\\/\\/promptapi.com\\/subscriptions to continue.\"}")
            )

            val response = services.getTimeSeries(
                "start date",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.message()).isNotEmpty()

        }

    @Test
    fun `TimeSeries fetchTimeSeriesData missing start date return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(501)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 501,\n" +
                                "        \"type\": \"no_timeframe_supplied\",\n" +
                                "        \"info\": \"You have not specified a Time-Frame. [Required format: ...&start_date=YYYY-MM-DD&end_date=YYYY-MM-DD]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.getTimeSeries(
                "",
                "end date",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(501)
        }
    }

    @Test
    fun `TimeSeries fetchTimeSeriesData missing end date return error`() {
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(501)
                    .setBody(
                        "{\n" +
                                "    \"success\": false,\n" +
                                "    \"error\": {\n" +
                                "        \"code\": 501,\n" +
                                "        \"type\": \"no_timeframe_supplied\",\n" +
                                "        \"info\": \"You have not specified a Time-Frame. [Required format: ...&start_date=YYYY-MM-DD&end_date=YYYY-MM-DD]\"\n" +
                                "    }\n" +
                                "}"
                    )
            )

            val response = services.getTimeSeries(
                "start date",
                "",
                "base currency",
                "selected currencies",
                "api key"
            )
            server.takeRequest()
            assertThat(response.code()).isEqualTo(501)
        }
    }
}