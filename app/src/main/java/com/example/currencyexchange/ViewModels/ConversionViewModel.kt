package com.example.currencyexchange.ViewModels

import androidx.lifecycle.*
import com.example.currencyexchange.Repository.Implementation.RetrofitRepositoryImplementation
import com.example.currencyexchange.BuildConfig
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.ConversionModel
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.Repository.Implementation.DatabaseRepositoryImplementation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val apiRepository: RetrofitRepositoryImplementation,
    private val databaseRepository: DatabaseRepositoryImplementation
) : ViewModel() {

    private val _exchangeState = MutableLiveData<DataWrapper<ConversionModel>>()
    val exchangeState: LiveData<DataWrapper<ConversionModel>> get() = _exchangeState

    val baseCurrency: SharedFlow<DataWrapper<CurrenciesDatabaseMain>> =
        databaseRepository.baseCurrency
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    val allCurrencies: SharedFlow<DataWrapper<CurrenciesDatabaseDetailed>> =
        databaseRepository.currencyData
            .map { DataWrapper.Success(it) }
            .catch { DataWrapper.Error(it.message) }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)


    fun exchangeCurrency(baseCurrency: String, selectedCurrency: String, amount: String) {
        viewModelScope.launch {
            val response = apiRepository.convertCurrency(
                baseCurrency = baseCurrency,
                wantedCurrency = selectedCurrency,
                amount = amount,
                apiKey = BuildConfig.API_KEY
            )
            try {
                if (response.isSuccessful){
                    _exchangeState.postValue(DataWrapper.Success(response.body()!!))
                }
            }catch (exception: Exception){
                _exchangeState.postValue(DataWrapper.Error(data = null, error = exception.message))
            }
        }
    }
}