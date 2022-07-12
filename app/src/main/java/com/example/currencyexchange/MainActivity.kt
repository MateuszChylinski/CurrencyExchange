package com.example.currencyexchange

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapter.CurrencyAdapter
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.ViewModel.CurrencyViewModel
import com.example.currencyexchange.ViewModel.CurrencyViewModelFactory

class MainActivity : AppCompatActivity() {

    private val retrofitService = ApiServices.getInstance()
    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_test)
        val adapter = CurrencyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this, CurrencyViewModelFactory(CurrencyRepository(retrofitService)))
            .get(CurrencyViewModel::class.java)
        viewModel.fetchLatestRates()
        viewModel.currencyRatesList.observe(this, Observer {
            adapter.setData(it.rates)
            recyclerView.adapter = adapter
            Log.d(TAG, "onCreate RATES: ${it.rates}")
        })
        viewModel.errorMessage.observe(this, Observer {
        })
    }
}