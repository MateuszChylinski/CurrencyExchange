package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.CurrencyAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding

//    TODO - add collapsing toolbar

class Latest : Fragment() {

    private val TAG = "Latest"
    private var mBaseCurrency: String = "default"
    lateinit var mViewModel: LatestViewModel

    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = CurrencyAdapter()

    private val mRetrofitService = ApiServices.getInstance()
    private var mDatabaseServices: CurrencyDatabaseRepository? = null

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

        mDatabaseServices = (activity?.application as CurrencyApplication).repository
        mViewModel =
            ViewModelProvider(
                this,
                LatestFactory(CurrencyRetrofitRepository(mRetrofitService), mDatabaseServices!!)
            ).get(LatestViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBaseCurrency()
    }

    private fun getBaseCurrency() {
        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
            mBaseCurrency = it.toString()
            if (mBaseCurrency != "default") {
                fetchFromViewModel()
            }
        })
    }

    private fun fetchFromViewModel() {
        mViewModel.fetchLatestRates(mBaseCurrency)
        mViewModel.latestRates.observe(viewLifecycleOwner, Observer {
            mBinding.latestBase.text = String.format("Base Currency: %s", mBaseCurrency)

//            TODO - what if user will change base currency? Does the previous one will be deleted along with the new one?
            if (it.latestRates.containsKey(mBaseCurrency)){
                it.latestRates.remove(mBaseCurrency)
            }
            mAdapter.setData(it.latestRates.toSortedMap())
        })
    }
}