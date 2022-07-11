package com.example.currencyexchange.ViewModel

import android.content.ContentValues.TAG
import android.telecom.Call
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.currencyexchange.Model.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyRepository
import retrofit2.Response
import javax.security.auth.callback.Callback

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
                Log.i(TAG, "onResponse: " + response.code())
                currencyRatesList.postValue(response.body())
                Log.i(TAG, "onResponse: $currencyRatesList")
            }

            override fun onFailure(call: retrofit2.Call<CurrencyModel>, t: Throwable) {
                Log.d(TAG, "onFailure: ERROR@@@@@@@@@@@@@@@@@@@@@@@@@@")
                errorMessage.postValue(t.message)
            }
        })

    }}
