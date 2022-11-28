package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.Models.CurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalArgumentException

class ConversionViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    val currencyList: LiveData<List<CurrencyNamesModel>> =
        databaseRepository.allCurrencies.asLiveData()
    var baseCurrency: LiveData<String> = databaseRepository.baseCurrency.asLiveData()
    var conversionResult = MutableLiveData<Double>()

    
    fun conversionCall(from: String, to: String, amount: String) {
        val response = apiRepository.convertCurrency(from, to, amount)
        response.enqueue(object : retrofit2.Callback<CurrencyModel> {
            override fun onResponse(call: Call<CurrencyModel>, response: Response<CurrencyModel>) {
                Log.i(TAG, "onResponse: CONVERSION ${response.code()}")
                if (response.isSuccessful) {
                    conversionResult.value = response.body()?.result
                }
            }

            override fun onFailure(call: Call<CurrencyModel>, t: Throwable) {
                Log.i(TAG, "onFailure (Conversion): ${t.message}")
            }
        })
    }
}

class ConversionFactory(
    private val retrofitRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConversionViewModel(retrofitRepository, databaseRepository) as T
        }
        throw IllegalArgumentException("UNKNOWN CAST FROM CONVERSION VIEW MODEL")
    }
}
