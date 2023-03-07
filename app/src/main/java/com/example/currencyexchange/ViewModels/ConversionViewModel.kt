package com.example.currencyexchange.ViewModels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalArgumentException

class ConversionViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

//    val currencyList: LiveData<List<CurrencyNamesModel>> =
//        databaseRepository.allCurrencies.asLiveData()
//    var baseCurrency: LiveData<String> = databaseRepository.baseCurrency.asLiveData()
    var conversionResult = MutableLiveData<ConversionModel?>()


//    fun conversionCall(from: String, to: String, amount: String) {
//        val response = apiRepository.convertCurrency(from, to, amount)
//        response.enqueue(object : retrofit2.Callback<ConversionModel> {
//            override fun onResponse(
//                call: Call<ConversionModel>,
//                response: Response<ConversionModel>
//            ) {
//                if (response.isSuccessful) {
//                    conversionResult.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<ConversionModel>, t: Throwable) {
//                Log.i(TAG, "onFailure (CONVERT) : ${t.message}")
//            }
//        })
//    }
//
//    fun getBaseCurrency(): String {
//        return baseCurrency.value.toString()
//    }
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
