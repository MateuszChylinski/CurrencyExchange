package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.LatestAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding

class Latest : Fragment() {

    private val TAG = "Latest"
    private var mBaseCurrency: String = "default"
    lateinit var mViewModel: LatestViewModel

    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = LatestAdapter()

    private val mRetrofitService = ApiServices.getInstance()
    private var mDatabaseServices: CurrencyDatabaseRepository? = null
    private var mIsRefreshed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLatestBinding = FragmentLatestBinding.inflate(layoutInflater)
        val view = mBinding.root

        mDatabaseServices = (activity?.application as CurrencyApplication).repository
        mViewModel =
            ViewModelProvider(
                this,
                LatestFactory(CurrencyRetrofitRepository(mRetrofitService), mDatabaseServices!!)
            ).get(LatestViewModel::class.java)

        mViewModel.mBaseCurrency.observe(viewLifecycleOwner, Observer {
            if (it != "default") {
                mBaseCurrency = it
                mBinding.latestBase.text =
                    String.format(getString(R.string.formatted_base_currency), mBaseCurrency)
                fetchRates()
            } else {
                Log.w(
                    TAG,
                    "onCreateView: Latest fragment. Base currency from database came as 'default.'"
                )
            }
        })
        mBinding.latestChangeBase.setOnClickListener {
//          TODO - do I need the args?
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
            it.findNavController().navigate(R.id.action_from_base_to_change)
        }
        /** Refresh fragment UI. Get base currency from the ViewModel. Perform new api call*/
        mBinding.latestRefreshContainer.setOnRefreshListener {
            mIsRefreshed = true

            mViewModel.getBaseCurrency()
            fetchRates()

            mBinding.latestRefreshContainer.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.latestRv.layoutManager = LinearLayoutManager(this.context)
        if (mBaseCurrency != "default") {
            fetchRates()
        }
    }

    /**  Perform an api call, and fetch rates data. Delete information about base currency, send data to adapter. */
    private fun fetchRates() {
        mViewModel.fetchLatestRates(mBaseCurrency)
        mViewModel.mLatestRates.observe(viewLifecycleOwner, Observer {
            if (it.latestRates.containsKey(mBaseCurrency)) {
                it.latestRates.remove(mBaseCurrency)
            }
            mBinding.latestRv.adapter = mAdapter
            mAdapter.setData(it.latestRates.toSortedMap())
        })
    }
}