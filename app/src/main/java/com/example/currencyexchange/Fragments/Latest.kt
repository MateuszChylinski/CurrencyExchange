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
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.CurrencyAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory
import kotlin.reflect.typeOf


class Latest : Fragment() {
    //VARIABLES
    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }
    private val currencies = mutableListOf<String>()
    private var mBaseCurrency: String = "default"
    private var mAllCurrencies: HashMap<String, Double> = hashMapOf()


    //    VIEWS
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: CurrencyAdapter? = null
    private var mBaseCurrencyTV: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_latest, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        TODO turn it on when finished navigation comp
//        getBaseCurrency()

        mBaseCurrencyTV = view.findViewById(R.id.latest_base)
        mRecyclerView = view.findViewById(R.id.latest_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        mAdapter = CurrencyAdapter()
        mRecyclerView?.adapter = mAdapter
    }

    private fun getBaseCurrency() {
        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
            mBaseCurrency = it.toString()
            if (mBaseCurrency != "default") {
                fetchFromViewModel()
            }
        })
    }

    private fun fetchFromViewModel() {
        mViewModel =
            ViewModelProvider(
                this,
                CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitService))
            )
                .get(CurrencyRetrofitViewModel::class.java)

        mViewModel.fetchLatestRates(mBaseCurrency)
        mViewModel.latestCurrencyRates.observe(viewLifecycleOwner, Observer {

            currencies.addAll(it.latestRates.keys)
            mAllCurrencies = it.latestRates

            populateDB(currencies)

            mAllCurrencies.remove(mBaseCurrency)
            mAdapter?.setData(it.latestRates)
            mBaseCurrencyTV?.text = String.format("Base currency: %s", mBaseCurrency)

        })

        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "fetchFromViewModel: RETROFIT VIEW MODEL ERROR!\n$it")
        })
    }

    private fun populateDB(currency: MutableList<String>) {
        val currIterator = currency.iterator()
        while (currIterator.hasNext()) {
            val curr = CurrencyNamesModel(currIterator.next())
            mDatabaseViewModel.insertNewCurrency(curr)
        }
    }
}