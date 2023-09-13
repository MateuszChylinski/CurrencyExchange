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
import com.example.currencyexchange.Models.CurrenciesDatabaseDetailed
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class Latest : Fragment() {
    private val TAG = "Latest"
    private var mBaseCurrency: String = ""
    private var mIsDatabaseEmpty = false
    private val mViewModel: LatestViewModel by activityViewModels()
    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = LatestAdapter()
    private var mCurrencyData: MutableList<CurrenciesDatabaseDetailed> = mutableListOf()

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
                mViewModel.fetchData(mBaseCurrency)
                mViewModel.latestRates.observe(viewLifecycleOwner, Observer { status ->
                    when (status) {
                        is DataWrapper.Success<*> -> {
                            // Add response to variable as mutable map, and find k/v for given base currency, remove it, and display.
                            val currencies = status.data?.latestRates?.toMutableMap()
                            currencies?.remove(mBaseCurrency)
                            mAdapter.setData(currencies!!)
                            mBinding.latestDate.text =
                                String.format(
                                    getString(R.string.rates_from_date),
                                    status.data.date
                                )
                            /** Find index of specific object in list. If it is present, then update is, if not, insert it into database
                             *  'mCurrencyData' is a list, that contains objects of 'CurrenciesDatabaseDetailed'.
                             *  It'll be used to maintain proper data about currencies, and display them in case where user will not have stable internet connection */
                            val indexToUpdate =
                                mCurrencyData.find { curr -> curr.baseCurrency == status.data.baseCurrency }?.id
                            if (indexToUpdate != null) {
                                mViewModel.updateCurrencies(
                                    CurrenciesDatabaseDetailed(
                                        id = indexToUpdate,
                                        baseCurrency = status.data.baseCurrency,
                                        ratesDate = status.data.date,
                                        currencyData = status.data.latestRates
                                    )
                                )
                            } else {
                                mViewModel.insertCurrencies(
                                    CurrenciesDatabaseDetailed(
                                        id = 0,
                                        baseCurrency = status.data.baseCurrency,
                                        ratesDate = status.data.date,
                                        currencyData = status.data.latestRates
                                    )
                                )
                            }
                            mBinding.latestBase.visibility = View.VISIBLE
                            mBinding.latestDate.visibility = View.VISIBLE
                            mBinding.latestProgressBar.visibility = View.INVISIBLE
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView: Failed to get latest rates:\n${status.message}"
                            )
                        }
                    }
                })
            }

        /** Create lazy coroutine that will add objects of 'CurrenciesDatabaseDetailed' to the list.
         * This list, will be needed in case when user will not have internet connection.
         * Program will find, and display proper object from this list.
         * In case when user will change base currency, while connection services are OFF, and the new base currency will not be in the list,
         * program will inform user that the database at current state doesn't contain data about specific base currency, so it'll be necessary to have internet connection */
        val currencyDataCoroutine =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                mViewModel.currencyDataList.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            currency.data?.forEach { currencyObject ->
                                mCurrencyData.add(
                                    CurrenciesDatabaseDetailed(
                                        currencyObject.id,
                                        currencyObject.baseCurrency,
                                        currencyObject.ratesDate,
                                        currencyObject.currencyData
                                    )
                                )
                            }
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView: couldn't retrieve currency data from database. ${currency.message}"
                            )
                        }
                    }
                }
            }

        /** Retrieve base currency from the database */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrency.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.latestBase.text = String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView getBaseCurrency Failed to retrieve the base currency from the database:\n${currency.message}"
                            )
                        }
                    }
                }
            }
        }

        /** Create lazy coroutine, which will be triggered in case,
         * where database contains data about currency rates, and mobile device does not have network connection */
        val oldRates =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                mViewModel.currencyDataList.collect { offlineCurrencies ->
                    when (offlineCurrencies) {
                        is DataWrapper.Success -> {
                            if (offlineCurrencies.data?.any { currency -> currency.baseCurrency == mBaseCurrency } == true) {
                                val index =
                                    offlineCurrencies.data.find { currency -> currency.baseCurrency == mBaseCurrency }?.id
                                val currencyData =
                                    offlineCurrencies.data[index!! - 1].currencyData.toMutableMap()
                                currencyData.remove(mBaseCurrency)
                                mAdapter.setData(currencyData)

                                mBinding.latestDate.text =
                                    String.format(
                                        getString(R.string.rates_from_date),
                                        offlineCurrencies.data[index - 1].ratesDate
                                    )

                                mBinding.latestBase.visibility = View.VISIBLE
                                mBinding.latestDate.visibility = View.VISIBLE
                                mBinding.latestRv.visibility = View.VISIBLE
                                mBinding.latestProgressBar.visibility = View.INVISIBLE

                            } else {
                                mBinding.latestProgressBar.visibility = View.INVISIBLE
                                mBinding.latestNoInternetSign.visibility = View.VISIBLE
                                mBinding.latestNoInternetExplanation.visibility =
                                    View.VISIBLE

                                mBinding.latestRv.visibility = View.INVISIBLE
                                mBinding.latestBase.visibility = View.INVISIBLE
                                mBinding.latestDate.visibility = View.INVISIBLE
                                mBinding.latestNoInternetExplanation.text =
                                    getString(R.string.no_network_after_changing_base)


                            }
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

        /** Create lazy coroutine that will collect flow with status of ConnectivityManager.
         *  This function is responsible for manipulating specific coroutines according to status of the internet connection
         *  Since ConnectivityManager is responsible for providing status of the connection services,
         *  it will not produce any state whenever user will start the app with connection services off.
         *  It will only produce any state after user will enable some connection services, like wifi.
         *  Before that, it is important to start appropriate coroutine, before the app will obtain any status from flow */

        val networkCoroutine =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {

                /*Since the flow will be emitted whenever user will turn on any of the connection services,
                firstly, behave like there is no internet connection. If the internet connection will be available, then perform an api call.
                if database is already populated with some data about currency rates start the 'oldRates' coroutine. */

                if (!mIsDatabaseEmpty) {
                    oldRates.start()
                    mBinding.latestNoInternetSign.visibility = View.INVISIBLE
                    mBinding.latestNoInternetExplanation.visibility = View.INVISIBLE

                    // if database is not populated, and mobile device does not have connection to the network, display explanation why user should enable network connection
                } else {
                    mBinding.latestNoInternetSign.visibility = View.VISIBLE
                    mBinding.latestNoInternetExplanation.visibility = View.VISIBLE
                    mBinding.latestBase.visibility = View.INVISIBLE
                    mBinding.latestDate.visibility = View.INVISIBLE
                }

                mViewModel.internetConnection.collect { status ->
                    /*if mobile device has network connection, cancel the 'oldRates' coroutine,
                     and start 'apiCallCoroutine', 'currencyDataCoroutine' coroutines. Manipulate visibility of the views. */
                    if (status.data?.name == "Available") {
                        oldRates.cancel()
                        apiCallCoroutine.start()
                        currencyDataCoroutine.start()

                        mBinding.latestNoInternetSign.visibility = View.INVISIBLE
                        mBinding.latestNoInternetExplanation.visibility = View.INVISIBLE
                        mBinding.latestRv.visibility = View.VISIBLE
                    }
                }
            }

        /** Check if database contains any values in it, and initiate 'mIsDbInit' variable with true/false value. Start 'networkCoroutine' to check if mobile device is connected to the network. */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.isDbInit.collect { dbStatus ->
                    when (dbStatus) {
                        is DataWrapper.Success -> {
                            mIsDatabaseEmpty = dbStatus.data!!
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
        }

        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database */
        mBinding.latestChangeBase.setOnClickListener {
            mBinding.latestBase.visibility = View.INVISIBLE
            mBinding.latestDate.visibility = View.INVISIBLE
            mBinding.latestRv.visibility = View.INVISIBLE

            val testVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            testVM.setMoveFlag(true)
        }
        return view
    }
}
