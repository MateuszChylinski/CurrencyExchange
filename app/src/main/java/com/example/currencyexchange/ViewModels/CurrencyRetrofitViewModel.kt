package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.Models.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalArgumentException

class CurrencyRetrofitViewModel constructor(private val CurrencyRetrofitRepository: CurrencyRetrofitRepository) :
    ViewModel() {

    val latestCurrencyRates = MutableLiveData<CurrencyModel>()
    val convertCurrencyData = MutableLiveData<CurrencyModel>()
    val errorMessage = MutableLiveData<String>()


    fun fetchLatestRates() {
        val response = CurrencyRetrofitRepository.fetchLatestRates()
        response.enqueue(object : retrofit2.Callback<CurrencyModel> {
            override fun onResponse(
                call: retrofit2.Call<CurrencyModel>, response: Response<CurrencyModel>
            ) {
                latestCurrencyRates.postValue(response.body())
            }

            override fun onFailure(call: retrofit2.Call<CurrencyModel>, t: Throwable) {
                Log.d(TAG, "onFailure: LATEST RATES ERROR")
                errorMessage.postValue(t.message)
            }
        })
    }

    fun convertCurrency() {
        val response = CurrencyRetrofitRepository.convertCurrency()
        response.enqueue(object : retrofit2.Callback<CurrencyModel> {
            override fun onResponse(call: Call<CurrencyModel>, response: Response<CurrencyModel>) {
                if (response.isSuccessful) {
                    convertCurrencyData.postValue(response.body())
                }
            }
            override fun onFailure(call: Call<CurrencyModel>, t: Throwable) {
                Log.d(TAG, "onFailure: CONVERT CURRENCY ERROR")
                errorMessage.postValue(t.message)
            }
        })
    }
}

class CurrencyViewModelFactory(private val repository: CurrencyRetrofitRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CurrencyRetrofitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CurrencyRetrofitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown retrofit ViewModel")
    }
}


