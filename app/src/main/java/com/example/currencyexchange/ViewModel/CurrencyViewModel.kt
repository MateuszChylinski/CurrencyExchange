package com.example.currencyexchange.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.Model.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyRepository
import retrofit2.Response

class CurrencyViewModel constructor(private val currencyRepository: CurrencyRepository) :
    ViewModel() {

    val currencyRatesList = MutableLiveData<CurrencyModel>()
    val errorMessage = MutableLiveData<String>()

    fun fetchLatestRates() {
        val response = currencyRepository.fetchLatestRates()
        response.enqueue(object : retrofit2.Callback<CurrencyModel> {
            override fun onResponse(
                call: retrofit2.Call<CurrencyModel>,
                response: Response<CurrencyModel>
            ) {
                currencyRatesList.postValue(response.body())
            }
            override fun onFailure(call: retrofit2.Call<CurrencyModel>, t: Throwable) {
                Log.d(TAG, "onFailure: ERROR@@@@@@@@@@@@@@@@@@@@@@@@@@")
                errorMessage.postValue(t.message)
            }
        })
    }}
