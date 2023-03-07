package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiResult
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.Adapters.LatestAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding
import kotlinx.coroutines.launch

class Latest : Fragment() {

    private val TAG = "Latest"
    lateinit var mViewModel: LatestViewModel

    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = LatestAdapter()
    private var mBaseCurrency: String = ""

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

        mDatabaseServices = (activity?.application as CurrencyApplication).repository
        mViewModel =
            ViewModelProvider(
                this,
                LatestFactory(CurrencyRetrofitRepository(mRetrofitService), mDatabaseServices!!)
            ).get(LatestViewModel::class.java)

        /** Launch new coroutine, and trigger ViewModel to make an api call
         *  Observe data that came as result from the api
         *  If the call was success, push obtained data to the adapter
         *  If call was NOT successful, log the error   */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.fetchData(mBaseCurrency)
                mViewModel.latestRates.observe(viewLifecycleOwner, Observer { status ->
                    when (status) {
                        is ApiResult.Success<*> -> {
                            mAdapter.setData(status.data?.latestRates!!)
                            Log.i(TAG, "onCreateView: " + status.data.latestRates.size)
                        }
                        is ApiResult.Error -> {
                            Log.w(
                                TAG,
                                "onCreateView: Failed to get latest rates:\n${status.throwable}"
                            )
                        }
                    }
                })
            }
        }

        /** Launch new coroutine, and trigger ViewModel to retrieve base currency from the database
         *  Observe base currency
         *  If ViewModel successfully retrieved base currency, set 'mBaseCurrency' with base currency from database. Set 'latestBase' text to display base currency
         *  If in ViewModel occur some error, display it in log*/
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.getBaseCurrency()
                mViewModel.baseCurrency.collect { currency ->
                    when (currency) {
                        is DatabaseState.Success<*> -> {
                            mBaseCurrency = currency.data.toString()
                            mBinding.latestBase.text = String.format(
                                getString(
                                    R.string.formatted_base_currency,
                                    mBaseCurrency
                                )
                            )
                        }
                        is DatabaseState.Error<*> -> {
                            Log.w(
                                TAG,
                                "Failed to retrieve the base currency from the database:\n${currency.error}"
                            )
                        }
                    }
                }
            }
        }

        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database*/
        mBinding.latestChangeBase.setOnClickListener {
            val testVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            testVM.setMoveFlag(true)
        }
        return view
    }
}