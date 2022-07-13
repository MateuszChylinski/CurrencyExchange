package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.APIs.ApiServices
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.ViewModels.CurrencyViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory

class Conversion : Fragment() {
    private lateinit var mViewModel: CurrencyViewModel
    private val mRetrofitService = ApiServices.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        test()


        return inflater.inflate(R.layout.fragment_conversion, container, false)

    }
    private fun test(){
        mViewModel = ViewModelProvider(this, CurrencyViewModelFactory(CurrencyRepository(mRetrofitService)))
            .get(CurrencyViewModel::class.java)
        mViewModel.convertCurrency()
        mViewModel.convertCurrencyData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "test: $it")
        })
    }

}

// TODO Fill spinners with all of the currencies from the api
