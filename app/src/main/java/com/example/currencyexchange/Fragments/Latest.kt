package com.example.currencyexchange.Fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.currencyexchange.databinding.FragmentLatestBinding


class Latest : Fragment() {

    private val TAG = "Latest"
    private var mBaseCurrency: String = "default"

    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = CurrencyAdapter()

    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLatestBinding = FragmentLatestBinding.inflate(layoutInflater)
        val view = mBinding.root

        mBinding.latestRv.layoutManager = LinearLayoutManager(this.context)
        mBinding.latestRv.adapter = mAdapter
        mBinding.latestRefreshContainer.setOnRefreshListener {
            getBaseCurrency()
            mBinding.latestRefreshContainer.isRefreshing = false
        }
        mBinding.latestChangeBaseCurrency.setOnClickListener {
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBaseCurrency()
    }

    private fun getBaseCurrency() {
        mDatabaseViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
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
            mBinding.latestBase.text = String.format("Base currency: %s", mBaseCurrency)

            if (it.latestRates.containsKey(mBaseCurrency)) {
                it.latestRates.remove(mBaseCurrency)
            }
            mAdapter.setData(it.latestRates)
            populateDB(it.latestRates.keys)
        })

        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Log.i(ContentValues.TAG, "fetchFromViewModel: RETROFIT VIEW MODEL ERROR!\n$it")
        })
    }

//    TODO - consider putting the currencies to the database in alph. Order
//    TODO - should I move it to the ViewModel, to relieve this fragment from any not view operations?
//    TODO - add collapsing toolbar
    private fun populateDB(currency: MutableSet<String>) {
        val currIterator = currency.iterator()
        while (currIterator.hasNext()) {
            val curr = CurrencyNamesModel(currIterator.next())
            mDatabaseViewModel.insertNewCurrency(curr)
        }
    }
}