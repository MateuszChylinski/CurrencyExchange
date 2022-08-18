package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.CurrencyAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.R
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
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: CurrencyAdapter? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mBase: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        TODO - disabled, due to testing toolbar.
        fetchFromViewModel()

        mBaseCurrencyTV = view.findViewById(R.id.latest_base)
        mRecyclerView = view.findViewById(R.id.latest_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        mAdapter = CurrencyAdapter()
        mRecyclerView?.adapter = mAdapter

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

            mBaseCurrencyTV?.text = ("Base currency: " + it.baseCurrency)
            mAdapter?.setData(it.latestRates)
//            Log.i(TAG, "fetchFromViewModel: \n${it.latestRates.keys}")

            val testIterator = it.latestRates.keys.iterator()
            while (testIterator.hasNext()) {
                currencyNames.add(testIterator.next())
            }
            populateDB()
        })


        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
//            Log.i(TAG, "fetchFromViewModel: ERROR")
        })
    }

    private fun  populateDB(){
        val currIterator = currencyNames.iterator()
        while (currIterator.hasNext()){
            val curr = CurrencyDatabaseModel(currIterator.next())
//            Log.i(TAG, "populateDB: "+curr)
            mDatabaseViewModel.insertNewCurrency(curr)
        }
    }
}