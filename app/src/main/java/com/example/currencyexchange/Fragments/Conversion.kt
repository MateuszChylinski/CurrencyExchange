package com.example.currencyexchange.Fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.ConversionViewModel
import com.example.currencyexchange.ViewModels.FragmentTagViewModel
import com.example.currencyexchange.databinding.FragmentConversionBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Conversion : Fragment() {
    private var mConversionBinding: FragmentConversionBinding? = null
    private val mBinding get() = mConversionBinding!!
    private val mViewModel: ConversionViewModel by activityViewModels()

    private var mCurrencyList: MutableList<String> = mutableListOf()
    private var mOfflineCurrencyList: MutableList<String> = mutableListOf()

    private val TAG = "Conversion"
    private var mBaseCurrency: String = "default"

    private var mDesiredCurrency: String = "default"
    private var mNetworkState = "default"
    private var mAmountToConversion = ""

    private var mResult: Double = 0.0
    private var mIsDbInit = false

    /** Prepare various of global coroutines.
     * The reason behind this, is because it can be used more than once,
     * which will be helpful with dealing with online/offline 'state' of the app.*/

    // make an api call, and display data that came out as an response
    private val mOnlineConversion: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.exchangeCurrency(
                baseCurrency = mBaseCurrency,
                selectedCurrency = mDesiredCurrency,
                amount = mAmountToConversion
            )
            mObserveResponse.start()
        }

    private val mObserveResponse: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.exchangeResult.observe(viewLifecycleOwner, Observer
            { conversion ->
                when (conversion) {
                    is DataWrapper.Success -> {
                        conversion.data?.result.let {

                            mResult = it.toString().toDouble()
                            mBinding.conversionConvertedData.text = String.format(getString(R.string.formatted_you_will_receive), mResult, mDesiredCurrency)

                            //After every api call, assign value of conversion in the ViewModel property, so it can survive orientation changes, and clear response data.
                            mViewModel.clearResponse()

                        }
                        mBinding.conversionProgressBar.visibility = View.GONE
                        mBinding.conversionProgressBar.visibility = View.INVISIBLE
                        mBinding.conversionConvertedData.visibility = View.VISIBLE
                    }

                    is DataWrapper.Error -> {
                        mBinding.conversionProgressBar.visibility = View.GONE
                        mBinding.conversionProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.timeout_explanation),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(
                            TAG,
                            "onCreateView: couldn't perform an api call to converse currencies. Exception: ${conversion.message}"
                        )
                    }

                    else -> {}
                }
            })
        }


    /* whenever device will not have internet connection,
     find proper data that was previously saved in database, and use it. */
    private val mOfflineConversion: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {

            mViewModel.currencyData.collect { currency ->
                when (currency) {
                    is DataWrapper.Success -> {
                        try {

                            // try to find data about given base currency
                            val index =
                                currency.data?.find { obj -> obj.baseCurrency == mBaseCurrency }?.id

                            // if database contain data about given currency make an conversion. If not, display warning.
                            if (index != null) {
                                currency.data.let {
                                    mResult =
                                        it[index - 1].currencyData[mDesiredCurrency].toString()
                                            .toDouble()
                                }


                                mBinding.conversionConvertedData.text = String.format(
                                    getString(R.string.formatted_you_will_receive),
                                    mResult * mAmountToConversion.toDouble(), mDesiredCurrency
                                )
                                mBinding.conversionConvertedData.visibility = View.VISIBLE

                            } else {
                                mBinding.conversionConvertedData.visibility = View.GONE
                                mBinding.conversionConvertedData.visibility = View.VISIBLE

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.no_network_after_changing_base),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            mBinding.conversionProgressBar.visibility = View.GONE
                            mBinding.conversionProgressBar.visibility = View.INVISIBLE
                            mBinding.conversionConvertedData.visibility = View.VISIBLE

                        } catch (exception: NullPointerException) {
                            Log.e(TAG, "onCreateView: mOfflineConversion error: $exception")
                        }
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve list of objects that contain data for specific base currency. Exception ${currency.message}",
                        )
                    }
                }
            }
        }

    // if device will have internet connection, load every currency that is available in the api
    private val mOnlineListOfCurrencies: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.allCurrencies.collect { currencies ->
                when (currencies) {
                    is DataWrapper.Success -> {
                        currencies.data?.let {
                            it[0].currencyData.keys.forEach { currency ->
                                mCurrencyList.add(currency)
                            }
                        }

                        if (!mCurrencyList.contains("Select currency")) {
                            mCurrencyList.add(0, "Select currency")
                        }
                        deleteBaseCurrency()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve all currencies from the database. ${currencies.message}",
                        )
                    }
                }
            }
        }

    /* if the device will not have internet connection,
    load list of currencies that are being stored in database,
    so user could make an conversion, but with old rates */
    private val mOfflineListOfCurrencies: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.currencyData.collect { currency ->
                when (currency) {
                    is DataWrapper.Success -> {
                        currency.data?.forEach { currenciesDatabaseDetailed ->
                            if (!mOfflineCurrencyList.contains(currenciesDatabaseDetailed.baseCurrency)) {
                                mOfflineCurrencyList.add(currenciesDatabaseDetailed.baseCurrency)
                            }
                        }

                        if (!mOfflineCurrencyList.contains("Select currency")) {
                            mOfflineCurrencyList.add(0, "Select currency")
                        }
                        deleteBaseCurrency()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve list of currencies needed to fill spinners with available offline conversion"
                        )
                    }
                }
            }
        }

    private val mObserveProvidedDesiredCurrency: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.convertTo.observe(viewLifecycleOwner, Observer {
                it?.let {
                    mDesiredCurrency = it
                    mBinding.conversionToTv.text =
                        String.format(
                            getString(R.string.formatted_to),
                            it
                        )
                }
            })
        }

    private val mObserveProvidedAmount: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.convertedAmount.observe(viewLifecycleOwner, Observer {
                it?.let {
                    mResult = it
                    mBinding.conversionConvertedData.text = String.format(
                        getString(R.string.formatted_you_will_receive),
                        mResult,
                        mDesiredCurrency
                    )
                }
            })
        }


    /* track network state. If the database doesn't contain any currency data,
        and there's no internet connection, display TextView with explanation
        if program will detect that internet connection is present, then stop offline currencies coroutine
        and start online coroutine to display every currency that is present in the api. */
    private val mNetworkStateCoroutine: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.networkState.collect { state ->
                when (state) {
                    is DataWrapper.Success -> {
                        mNetworkState = state.data?.name.toString()

                        if (mNetworkState == "Available" || !mIsDbInit) {
                            mOfflineListOfCurrencies.cancel()
                            mOnlineListOfCurrencies.start()
                            mBinding.conversionAppbar.visibility = View.VISIBLE
                        } else {
                            mBinding.conversionError.visibility = View.VISIBLE
                        }
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve state of the network services. Exception: ${state.message}",
                        )
                    }
                }
            }
        }

    private val mGetBaseCurrency: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.baseCurrency.collect { currency ->
                        when (currency) {
                            is DataWrapper.Success -> {
                                mBaseCurrency = currency.data?.baseCurrency!!
                                mBinding.conversionFromTv.text =
                                    String.format(getString(R.string.formatted_from), mBaseCurrency)
                            }

                            is DataWrapper.Error -> {
                                Log.e(
                                    TAG,
                                    "onCreateView: couldn't retrieve base currency from the database. ${currency.message}"
                                )
                            }
                        }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mConversionBinding = FragmentConversionBinding.inflate(layoutInflater)
        val view = mBinding.root

        // track database state (is it empty, or not). Start appropriate coroutines
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.databaseState.collect { state ->
                    when (state) {
                        is DataWrapper.Success -> {
                            state.data.let {
                                mIsDbInit = it.toString().toBoolean()
                            }
                            if (mIsDbInit) {
                                mBinding.conversionError.visibility = View.VISIBLE
                                mBinding.conversionFromTv.visibility = View.GONE
                                mBinding.conversionFromTv.visibility = View.INVISIBLE
                                mBinding.conversionAppbar.visibility = View.GONE
                                mBinding.conversionAppbar.visibility = View.INVISIBLE
                                mBinding.conversionFromSpinner.visibility = View.GONE
                                mBinding.conversionFromSpinner.visibility = View.INVISIBLE
                                mBinding.conversionToTv.visibility = View.GONE
                                mBinding.conversionToTv.visibility = View.INVISIBLE
                                mBinding.conversionToSpinner.visibility = View.GONE
                                mBinding.conversionToSpinner.visibility = View.INVISIBLE
                                mBinding.conversionEnterValueTv.visibility = View.GONE
                                mBinding.conversionEnterValueTv.visibility = View.INVISIBLE
                                mBinding.conversionEnterValue.visibility = View.GONE
                                mBinding.conversionEnterValue.visibility = View.INVISIBLE
                                mBinding.conversionConverseBtn.visibility = View.GONE
                                mBinding.conversionConverseBtn.visibility = View.INVISIBLE
                            } else {
                                mBinding.conversionError.visibility = View.GONE
                                mBinding.conversionError.visibility = View.INVISIBLE
                                mBinding.conversionFromTv.visibility = View.VISIBLE
                                mBinding.conversionFromSpinner.visibility = View.VISIBLE
                                mBinding.conversionToTv.visibility = View.VISIBLE
                                mBinding.conversionToSpinner.visibility = View.VISIBLE
                                mBinding.conversionEnterValueTv.visibility = View.VISIBLE
                                mBinding.conversionEnterValue.visibility = View.VISIBLE
                                mBinding.conversionConverseBtn.visibility = View.VISIBLE
                            }
                            mGetBaseCurrency.start()
                            mNetworkStateCoroutine.start()
                            mOfflineListOfCurrencies.start()
                            mObserveProvidedAmount.start()
                            mObserveProvidedDesiredCurrency.start()
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView: couldn't retrieve state of the database. Exception: ${state.message}",
                            )
                        }
                    }
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Prepare a flag, to let ViewPager host fragment know, when it should navigate user to
          'ChangeBaseCurrency' fragment. */
        mBinding.conversionChangeBaseCurrency.setOnClickListener {
            val mFragmentVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            mFragmentVM.setMoveFlag(true)
        }

        /* Retrieve provided amount in EditText, and make an api call, based on:
        base currency, desired currency, and amount */
        mBinding.conversionConverseBtn.setOnClickListener {
            // If any of the currencies (from/to) are set to 'default', or given input from amount EditText view is lower than 1, then display Toast, which will inform user about providing proper data in order to make a conversion
            val defaultCurrencies = mBaseCurrency == "default" || mDesiredCurrency == "default"
            val isConversionEmpty = mBinding.conversionEnterValue.text.toString().isEmpty()

            if (defaultCurrencies || isConversionEmpty
            ) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.select_desired_currency),
                    Toast.LENGTH_LONG
                ).show()
            } else {

                mAmountToConversion = mBinding.conversionEnterValue.text.toString()
                mBinding.conversionProgressBar.visibility = View.VISIBLE

                if (mNetworkState == "Available") {
                    mOnlineConversion.start()
                } else {
                    mOfflineConversion.start()
                }
            }
        }

        /* Prepare layout to state as it was when user first entered it. Clear currency lists,
           and start one more time appropriate coroutines */
        mBinding.conversionRefreshContainer.setOnRefreshListener {
            mBinding.conversionEnterValue.text.clear()
            mBinding.conversionConvertedData.text = ""
            mBinding.conversionConvertedData.visibility = View.GONE
            mBinding.conversionConvertedData.visibility = View.INVISIBLE
            mBinding.conversionError.visibility = View.GONE
            mBinding.conversionError.visibility = View.INVISIBLE

            mBinding.conversionProgressBar.visibility = View.GONE
            mBinding.conversionProgressBar.visibility = View.INVISIBLE
            mBinding.conversionToTv.text = getString(R.string.currency_name)

            mOfflineCurrencyList.clear()
            mCurrencyList.clear()

            mDesiredCurrency = "default"
            mAmountToConversion = ""

            mGetBaseCurrency.start()
            mNetworkStateCoroutine.start()
            mOfflineListOfCurrencies.start()

            mBinding.conversionRefreshContainer.isRefreshing = false
        }


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mBinding.conversionConvertedData.visibility = View.VISIBLE
    }

    /* Copy list of all currencies,
       check if given list contains base currency, and desired currency (if user have already picked one).
       If list contains these currencies, delete it, so user will not see them in spinner anymore. */
    private fun deleteBaseCurrency() {
        val copiedList: MutableList<String> = mutableListOf()
        copiedList.clear()

        if (mNetworkState == "Available") {
            mCurrencyList.forEach { currency ->
                copiedList.add(currency)
            }
        } else {
            mOfflineCurrencyList.forEach { currency ->
                copiedList.add(currency)
            }
        }

        copiedList.removeIf {
            it == mBaseCurrency || it == mDesiredCurrency
        }

        prepareFromSpinner(copiedList)
        prepareToSpinner(copiedList)
    }

    // Prepare 'from' spinner. This spinner allow to pick new, temporary base currency
    private fun prepareFromSpinner(currencyList: MutableList<String>) {
        var isTouched = false
        val fromAdapter =
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                currencyList
            )
        mBinding.conversionFromSpinner.adapter = fromAdapter
        mBinding.conversionFromSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (currencyList[position] == "Select currency") isTouched = false
                    if (isTouched) {
                        isTouched = false
                        mBaseCurrency = currencyList[position]
                        mBinding.conversionFromTv.text =
                            String.format(
                                getString(R.string.formatted_from),
                                mBaseCurrency
                            )
                        deleteBaseCurrency()
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.e(TAG, "onNothingSelected in 'from' spinner")
                }
            }
    }

    /* Prepare 'to' spinner.
       Picked currency from this spinner, will be marked as desired currency in api call later on  */
    private fun prepareToSpinner(currencyList: MutableList<String>) {
        var isTouched = false
        val toAdapter =
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                currencyList
            )
        mBinding.conversionToSpinner.adapter = toAdapter
        mBinding.conversionToSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (currencyList[position] == "Select currency") isTouched = false
                    if (isTouched) {
                        isTouched = false
                        mDesiredCurrency = currencyList[position]
                        mBinding.conversionEnterValue.text.clear()
                        mBinding.conversionToTv.text =
                            String.format(
                                getString(R.string.formatted_to),
                                mDesiredCurrency
                            )
                        deleteBaseCurrency()
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.e(TAG, "onNothingSelected: NOTHING SELECTED!")
                }
            }
    }
}
