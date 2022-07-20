package com.example.currencyexchange.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory


class Latest : Fragment() {
    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fetchFromViewModel()
        return inflater.inflate(R.layout.fragment_latest, container, false)
    }

    private fun fetchFromViewModel() {
        mViewModel =
            ViewModelProvider(this, CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitService)))
                .get(CurrencyRetrofitViewModel::class.java)
        mViewModel.fetchLatestRates()
        mViewModel.latestCurrencyRates.observe(viewLifecycleOwner, Observer {

        })
        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
        })
    }
}