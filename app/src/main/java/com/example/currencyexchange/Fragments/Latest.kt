package com.example.currencyexchange.Fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.CurrencyAdapter
import com.example.currencyexchange.Adapters.PagerAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory


class Latest : Fragment() {

//    TODO Add collapsing toolbar
    //VARIABLES
    private val TAG = "Latest"
    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }
    private val currencies = mutableListOf<String>()
    private var mBaseCurrency: String = "default"
    private var mAllCurrencies: HashMap<String, Double> = hashMapOf()
    private var mIsCallCanceled: Boolean = false

    //    VIEWS
    private var mRefreshContainer: SwipeRefreshLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: CurrencyAdapter? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mChangeBaseIcon: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_latest, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRefreshContainer = view.findViewById(R.id.latest_refresh_container)
        mBaseCurrencyTV = view.findViewById(R.id.latest_base)
        mRecyclerView = view.findViewById(R.id.latest_rv)
        mRecyclerView?.layoutManager = LinearLayoutManager(this.context)
        mAdapter = CurrencyAdapter()
        mRecyclerView?.adapter = mAdapter
        mChangeBaseIcon = view.findViewById(R.id.latest_change_base_currency)
        mChangeBaseIcon?.setOnClickListener {
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
        }
        getBaseCurrency()

        mRefreshContainer?.setOnRefreshListener {
            getBaseCurrency()
            mRefreshContainer?.isRefreshing = false
        }
        mBaseCurrencyTV?.setOnClickListener {
            mIsCallCanceled = true
        }
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

        mViewModel.fetchLatestRates(mBaseCurrency, mIsCallCanceled)
        mViewModel.latestCurrencyRates.observe(viewLifecycleOwner, Observer {


            currencies.addAll(it.latestRates.keys)
            mAllCurrencies = it.latestRates

            populateDB(currencies)

            mAllCurrencies.remove(mBaseCurrency)
            mAdapter?.setData(it.latestRates)
            mBaseCurrencyTV?.text = String.format("Base currency: %s", mBaseCurrency)
        })

        mViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Log.i(ContentValues.TAG, "fetchFromViewModel: RETROFIT VIEW MODEL ERROR!\n$it")
        })
    }

//    TODO consider putting the currencies to the database in alph. Order
    private fun populateDB(currency: MutableList<String>) {
        val currIterator = currency.iterator()
        while (currIterator.hasNext()) {
            val curr = CurrencyNamesModel(currIterator.next())
            mDatabaseViewModel.insertNewCurrency(curr)
        }
    }
}