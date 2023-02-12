package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.FluctuationRates
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FluctuationViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    var baseCurrency = databaseRepository.baseCurrency.asLiveData()
    var allCurrencies = databaseRepository.allCurrencies.asLiveData()

    var startDate: String = "default"
    var endDate: String = "default"

    var data = MutableLiveData<Map<String, FluctuationRates>?>()

    // Fetch the data from the server, and store it in 'data' variable, which can be observer in fragment.
    fun fetchFluctuation(baseCurrency: String, selectedCurrencies: String) {
        viewModelScope.launch {
            val response =
                apiRepository.fetchFluctuation(startDate, endDate, baseCurrency, selectedCurrencies)
            response.enqueue(object : retrofit2.Callback<FluctuationModel> {
                override fun onResponse(
                    call: Call<FluctuationModel>,
                    response: Response<FluctuationModel>
                ) {
                    Log.i(TAG, "onResponse: RETROFIT\n${response.body()}")
                    if (response.isSuccessful) {
                        data.value = response.body()?.rates
                    }
                }
                override fun onFailure(call: Call<FluctuationModel>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                }
            })
        }
    }
    // TODO - do I need it? Isn't it "clear" itself after each api call?
    fun clearResponseData() {
        data.value = null
    }
    fun getBaseCurrency(): String {
        return baseCurrency.value.toString()
    }
}

class FluctuationFactory(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FluctuationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FluctuationViewModel(apiRepository, databaseRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN CAST FROM FLUCTUATION FACTORY")
    }
}