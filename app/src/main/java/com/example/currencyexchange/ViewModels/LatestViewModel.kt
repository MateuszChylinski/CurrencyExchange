package com.example.currencyexchange.ViewModels

import androidx.lifecycle.*
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Models.LatestRates
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalArgumentException

class LatestViewModel constructor(
    private val apiRepository: CurrencyRetrofitRepository,
    private val databaseRepository: CurrencyDatabaseRepository
) : ViewModel() {

    var mLatestRates = MutableLiveData<LatestRates>()
    var mLatestError = MutableLiveData<String>()
    var mCurrenciesSet = sortedSetOf<String>()
    var mBaseCurrency = databaseRepository.baseCurrency.asLiveData()

    fun fetchLatestRates(baseCurrency: String) {
        val response = apiRepository.fetchLatestRates(baseCurrency)
        response.enqueue(object : retrofit2.Callback<LatestRates> {
            override fun onResponse(call: Call<LatestRates>, response: Response<LatestRates>) {
                if (response.isSuccessful) {
                    mLatestRates.postValue(response.body())
                    response.body()?.latestRates?.keys?.let { mCurrenciesSet.addAll(it) }
                    if (mCurrenciesSet.size > 0) {
                        viewModelScope.launch { populateDB() }
                    }
                }
            }
            override fun onFailure(call: Call<LatestRates>, t: Throwable) {
                mLatestError.postValue(t.message)
            }
        })
    }

    fun getBaseCurrency(): String{
        return mBaseCurrency.value.toString()
    }

    suspend fun populateDB() {
        val iterator = mCurrenciesSet.iterator()
        while (iterator.hasNext()) {
            val curr = CurrencyNamesModel(iterator.next())
            databaseRepository.insertNewCurrency(curr)
        }
    }
}

class LatestFactory(
    val retrofitRepository: CurrencyRetrofitRepository,
    val databaseRepository: CurrencyDatabaseRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LatestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LatestViewModel(retrofitRepository, databaseRepository) as T
        }
        throw IllegalArgumentException("Unknown retrofit ViewModel")
    }
}