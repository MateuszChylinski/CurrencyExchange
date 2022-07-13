package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.Model.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyRepository
import retrofit2.Call
import retrofit2.Response

class CurrencyViewModel constructor(private val currencyRepository: CurrencyRepository) :
    ViewModel() {

    val latestCurrencyRates = MutableLiveData<CurrencyModel>()
    val convertCurrencyData = MutableLiveData<CurrencyModel>()

    val errorMessage = MutableLiveData<String>()

    fun fetchLatestRates() {
        val response = currencyRepository.fetchLatestRates()
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
        val response = currencyRepository.convertCurrency()
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


