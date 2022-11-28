package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.provider.Telephony.Mms.Rate
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Models.FluctuationModel
import com.example.currencyexchange.Models.Rates
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback
import kotlin.math.log

class FluctuationViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    var baseCurrency = databaseRepository.baseCurrency.asLiveData()
    var allCurrencies = databaseRepository.allCurrencies.asLiveData()

    var startDate: String = "default"
    var endDate: String = "default"
    var selectedCurrencies: String = "default"
    var isDone = MutableLiveData<Boolean>()

    var currenciesNames = MutableLiveData<String>()
    var currenciesStartRates = MutableLiveData<Double>()
    var currenciesEndRates = MutableLiveData<Double>()
    var currenciesChange = MutableLiveData<Double>()
    var currenciesChangePct = MutableLiveData<Double>()

    fun fetchFluctuation(baseCurrency: String) {
        viewModelScope.launch {
            val response = apiRepository.fetchFluctuation(startDate, endDate, baseCurrency, selectedCurrencies)
            response.enqueue(object : retrofit2.Callback<FluctuationModel> {
                override fun onResponse(call: Call<FluctuationModel>, response: Response<FluctuationModel>) {
                    if (response.isSuccessful){
                        Log.i(TAG, "onResponse: TEST ${response.body()?.base}")
                        for (i in response.body()?.rates?.keys!!){
                            currenciesNames.value = i
                            currenciesStartRates.value = response.body()?.rates?.getValue(i)?.start_rate
                            currenciesEndRates.value = response.body()?.rates?.getValue(i)?.end_rate
                            currenciesChange.value = response.body()?.rates?.getValue(i)?.change
                            currenciesChangePct.value = response.body()?.rates?.getValue(i)?.change_pct
                        }
                        isDone.value = true
                    }
                }
                override fun onFailure(call: Call<FluctuationModel>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                }
            })
        }
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