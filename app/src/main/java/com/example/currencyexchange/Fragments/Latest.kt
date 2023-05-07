package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Adapters.LatestAdapter
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import com.example.currencyexchange.NetworkDetection.ObserverImplementation

class Latest : Fragment() {
    private val TAG = "Latest"
    private val mViewModel: LatestViewModel by activityViewModels()
    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = LatestAdapter()
    private var mBaseCurrency: String = ""
    private var mIsDbInit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLatestBinding = FragmentLatestBinding.inflate(layoutInflater)
        val view = mBinding.root

        mBinding.latestRv.layoutManager = LinearLayoutManager(this.context)
        mBinding.latestRv.adapter = mAdapter

        /** Create lazy coroutine, which will be triggered whenever mobile device will have network connection. Perform an api call, and observe values that came from the call. */
        val apiCallCoroutine =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.fetchData(mBaseCurrency)
                    mViewModel.latestRates.observe(viewLifecycleOwner, Observer { status ->
                        when (status) {
                            is DataWrapper.Success<*> -> {
                                // Convert currencies from the database to the mutable map, and remove base currency with it's value, push modified data to the adapter
                                val currenciesWithoutBase =
                                    status.data?.latestRates!!.toMutableMap()
                                currenciesWithoutBase.remove(mBaseCurrency)
                                mAdapter.setData(currenciesWithoutBase)
                                mBinding.latestDate.text = String.format(
                                    getString(R.string.rates_from_date),
                                    status.data.date
                                )
                            }

                            is DataWrapper.Error -> {
                                Log.w(
                                    TAG,
                                    "onCreateView: Failed to get latest rates:\n${status.message}"
                                )
                            }
                        }
                    })
                }
            }

        /** Create lazy coroutine, which will be triggered in case where database contains data about currency rates, and mobile device does not have network connection    */
        val oldRates = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.currenciesDataState.collect { offlineCurrencies ->
                when (offlineCurrencies) {
                    is DataWrapper.Success -> {
                        val currenciesWithoutBase =
                            offlineCurrencies.data?.currencyData!!.toMutableMap()
                        currenciesWithoutBase.remove(mBaseCurrency)
                        mAdapter.setData(currenciesWithoutBase)
                        mBinding.latestDate.text = String.format(
                            getString(R.string.rates_from_date),
                            offlineCurrencies.data.ratesDate
                        )
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve currency rates from the database ${offlineCurrencies.message}"
                        )
                    }
                }
            }
        }

        /** Launch coroutine that will retrieve main data about currency. */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrencyState.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.latestBase.text = String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                        }

                        is DataWrapper.Error -> {
                            Log.w(
                                TAG,
                                "onCreateView getBaseCurrency Failed to retrieve the base currency from the database:\n${currency.message}"
                            )
                        }
                    }
                }
            }
        }
        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database */
        mBinding.latestChangeBase.setOnClickListener {
            val testVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            testVM.setMoveFlag(true)
        }

        /** Create a lazy coroutine that will observe connection services. */
        val networkCoroutine =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                val networkStatus = ObserverImplementation(requireContext())

                // if database is already populated with some data about currency rates stop the 'apiCallCoroutine', and start the 'oldRates' coroutine.
                if (!mIsDbInit) {
                    if (apiCallCoroutine.isActive) {
                        apiCallCoroutine.cancel()
                    }
                    oldRates.start()
                    mBinding.latestNoInternetSign.visibility = View.INVISIBLE
                    mBinding.latestNoInternetExplanation.visibility = View.INVISIBLE
                    mBinding.appBarLayout.visibility = View.VISIBLE
                    mBinding.latestBase.visibility = View.VISIBLE
                    mBinding.latestDate.visibility = View.VISIBLE

                    // if database is not populated, and mobile device does not have connection to the network, display explanation why user should enable network connection
                } else {
                    mBinding.latestNoInternetSign.visibility = View.VISIBLE
                    mBinding.latestNoInternetExplanation.visibility = View.VISIBLE
                    mBinding.appBarLayout.visibility = View.INVISIBLE
                    mBinding.latestBase.visibility = View.INVISIBLE
                    mBinding.latestDate.visibility = View.INVISIBLE
                }
                networkStatus.observeStatus().collect { status ->
                    // if mobile device has network connection, cancel the 'oldRates' coroutine, and start 'apiCallCoroutine' coroutine. Manipulate visibility of the views.
                    if (status.name == "Available") {
                        if (oldRates.isActive) {
                            oldRates.cancel()
                        }
                        apiCallCoroutine.start()

                        mBinding.latestNoInternetSign.visibility = View.INVISIBLE
                        mBinding.latestNoInternetExplanation.visibility = View.INVISIBLE

                        mBinding.appBarLayout.visibility = View.VISIBLE
                        mBinding.latestBase.visibility = View.VISIBLE
                        mBinding.latestDate.visibility = View.VISIBLE
                    }
                }
            }

        /** Check if database contains any values in it, and initiate 'mIsDbInit' variable with true/false value. Start 'networkCoroutine' to check if mobile device is connected to the network. */
        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.isDbInit.collect { dbStatus ->
                when (dbStatus) {
                    is DataWrapper.Success -> {
                        mIsDbInit = dbStatus.data!!
                        networkCoroutine.start()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve database status. ${dbStatus.message}"
                        )
                    }
                }
            }
        }
        return view
    }
}