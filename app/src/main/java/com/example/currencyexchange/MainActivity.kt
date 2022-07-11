package com.example.currencyexchange

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.ViewModel.CurrencyViewModel
import com.example.currencyexchange.ViewModel.CurrencyViewModelFactory

class MainActivity : AppCompatActivity() {

    private val retrofitService = ApiServices.getInstance()
    lateinit var viewModel: CurrencyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, CurrencyViewModelFactory(CurrencyRepository(retrofitService)))
            .get(CurrencyViewModel::class.java)

        viewModel.fetchLatestRates()
        viewModel.currencyRatesList.observe(this, Observer {
            Log.d(TAG, "onCreate: $it")
        })
        viewModel.errorMessage.observe(this, Observer {
        })
    }
}