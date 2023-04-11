//package com.example.currencyexchange.ViewModels
//
//import android.content.ContentValues.TAG
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.currencyexchange.API.DatabaseState
//import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
//import com.example.currencyexchange.Models.CurrenciesDatabaseMain
//import com.example.currencyexchange.Repository.Implementation.CurrencyDatabaseRepository
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//
//class ChangeBaseViewModel constructor(
//    private val databaseRepository: CurrencyDatabaseRepository
//) : ViewModel() {
//
//    val baseCurrencyState: SharedFlow<DatabaseState<CurrenciesDatabaseMain>> =
//        databaseRepository.baseCurrency
//            .catch { DatabaseState.Error(it.message) }
//            .map { DatabaseState.Success(it) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)
//
//    val currencyNames: SharedFlow<DatabaseState<CurrenciesDatabaseDetailed>> =
//        databaseRepository.currencyData
//            .catch { DatabaseState.Error(it.message) }
//            .map { DatabaseState.Success(it) }
//            .shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)
//
//
//    fun updateBaseCurrency(currency: String?) {
//        viewModelScope.launch {
//            try {
//                databaseRepository.updateBaseCurrency(currency)
//            } catch (exception: Exception) {
//                Log.e(
//                    TAG,
//                    "updateBaseCurrency: Failed to update base currency in $TAG\n${exception.message}"
//                )
//            }
//        }
//    }
//}
//
//class ChangeBaseFactory(
//    private val currencyDatabaseRepository: CurrencyDatabaseRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ChangeBaseViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ChangeBaseViewModel(currencyDatabaseRepository) as T
//        }
//        throw IllegalArgumentException("UNKNOWN VIEW MODEL CAST FROM CHANGE BASE")
//    }
//}