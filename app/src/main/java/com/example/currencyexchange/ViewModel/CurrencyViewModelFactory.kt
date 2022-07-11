package com.example.currencyexchange.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.Model.CurrencyModel
import com.example.currencyexchange.Repository.CurrencyRepository
import java.lang.IllegalArgumentException

class CurrencyViewModelFactory constructor(private val repository: CurrencyRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CurrencyViewModel::class.java)) {
            CurrencyViewModel(this.repository) as T
        }else{
            throw IllegalArgumentException("Couldn't find ViewModel")
        }
    }
}