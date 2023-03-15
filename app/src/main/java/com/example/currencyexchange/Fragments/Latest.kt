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

    private val mRetrofitService = ApiServices.getInstance()
    private var mDatabaseServices: CurrencyDatabaseRepository? = null

    private var mBaseCurrency: String = ""
    private val mAdapter = LatestAdapter()

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


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrencyState.collect { currency ->
                    when (currency) {
                        is DatabaseState.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.latestBase.text = String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                            mBinding.latestDate.text = String.format(
                                getString(R.string.rates_from_date),
                                currency.data?.ratesDate.toString()
                            )
                        }
                        is DatabaseState.Error -> {
                            Log.w(
                                TAG,
                                "onCreateView getBaseCurrency Failed to retrieve the base currency from the database:\n${currency.message}"
                            )
                        }
                    }
                }
            }
        }

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

//        /** Will be used when there's no internet connection    */
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mViewModel.currenciesDataState.collect { currencyData ->
//                    when (currencyData) {
//                        is DatabaseState.Success -> {
//                            Log.i(TAG, "onCreateView: "+currencyData.data?.currencyData?.entries)
//                        }
//                        is DatabaseState.Error -> {
//                            Log.e(
//                                TAG,
//                                "onCreateView: Failed to get currency data\n${currencyData.message}",
//                            )
//                        }
//                    }
//                }
//            }
//        }

        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database */
        mBinding.latestChangeBase.setOnClickListener {
            val testVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            testVM.setMoveFlag(true)
        }
        return view
    }
}