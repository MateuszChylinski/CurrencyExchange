package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.Models.HistoricalRatesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class HistoricalViewModel constructor(
    private val retrofitRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {
    var date: String = "default"
    var historicalData  = MutableLiveData<HistoricalRatesModel?>()

//    val mBaseCurrency = databaseRepository.baseCurrency.asLiveData()
//    val currencyList = databaseRepository.allCurrencies.asLiveData()

    fun fetchHistoricalData(baseCurrency: String, selectedCurrencies: String) {
//        viewModelScope.launch {
//            val response =
//                retrofitRepository.fetchHistoricalData(date, selectedCurrencies, baseCurrency)
//            response.enqueue(object : retrofit2.Callback<HistoricalRatesModel> {
//                override fun onResponse(
//                    call: Call<HistoricalRatesModel>,
//                    response: Response<HistoricalRatesModel>
//                ) {
//                    if (response.isSuccessful) {
//                        historicalData.value = response.body()
//                    }
//                }
//                override fun onFailure(call: Call<HistoricalRatesModel>, t: Throwable) {
//                    Log.i(TAG, "onFailure: FETCHING HISTORICAL DATA ERROR\n${t.message}")
//                }
//            })
//        }
    }
//    fun getBaseCurrency (): String{
//        return mBaseCurrency.value.toString()
//    }
    fun clearApiResponse(){
        historicalData.value = null
    }
}

class HistoricalFactory(
    private val retrofitRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoricalViewModel(retrofitRepository, databaseRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN CAST FROM HISTORICAL VIEW MODEL FACTORY")
    }
}