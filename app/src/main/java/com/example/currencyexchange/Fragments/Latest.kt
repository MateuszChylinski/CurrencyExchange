package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory


class Latest : Fragment() {
    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }
    private val currencyNames = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fetchFromViewModel()
        return inflater.inflate(R.layout.fragment_latest, container, false)
    }

    private fun fetchFromViewModel() {
        mViewModel =
            ViewModelProvider(
                this,
                CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitService))
            )
                .get(CurrencyRetrofitViewModel::class.java)

        mViewModel.fetchLatestRates()
        mViewModel.latestCurrencyRates.observe(viewLifecycleOwner, Observer {
            val testIterator = it.latestRates.keys.iterator()
            while (testIterator.hasNext()) {
                currencyNames.add(testIterator.next())
            }
            populateDB()
        })
        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
        })
    }

    private fun populateDB() {
        if (currencyNames.size > 1) {
            currencyNames.forEach{
                val model = CurrencyDatabaseModel(it)
                mDatabaseViewModel.insertNewCurrency(model)
            }
        }
    }
}