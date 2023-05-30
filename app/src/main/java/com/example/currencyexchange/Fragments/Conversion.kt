package com.example.currencyexchange.Fragments

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
    private val TAG = "Conversion"
    private var mBaseCurrency: String = "default"
    private var mDesiredCurrency: String = "default"
    private var mNetworkState = "default"
    private var mAmountToConversion = ""
    private var mIsDbInit = false

    private var mCurrencyList: MutableList<String> = mutableListOf()
    private var mOfflineCurrencyList: MutableList<String> = mutableListOf()

    private var mConversionBinding: FragmentConversionBinding? = null
    private val mBinding get() = mConversionBinding!!
    private val mViewModel: ConversionViewModel by activityViewModels()

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
            mViewModel.conversionCall.observe(viewLifecycleOwner, Observer { conversion ->
                when (conversion) {
                    is DataWrapper.Success -> {
                        mBinding.conversionConvertedData.visibility = View.VISIBLE
                        mBinding.conversionConvertedData.text = String.format(
                            getString(R.string.formatted_you_will_receive),
                            conversion.data?.result,
                            conversion.data?.query?.to
                        )
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't perform an api call to converse currencies. Exception: ${conversion.message}"
                        )
                    }
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
                            val index =
                                currency.data?.find { obj -> obj.baseCurrency == mBaseCurrency }?.id
                            val result =
                                currency.data?.get(index!! - 1)?.currencyData?.get(
                                    mDesiredCurrency
                                )

                            mBinding.conversionConvertedData.visibility = View.VISIBLE
                            mBinding.conversionConvertedData.text = String.format(
                                getString(R.string.formatted_you_will_receive),
                                result!! * mAmountToConversion.toInt(),
                                mDesiredCurrency
                            )

                        } catch (exception: NullPointerException) {
                            Log.e(TAG, "onCreateView: ERROR $exception")
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.allCurrencies.collect { currencies ->
                    when (currencies) {
                        is DataWrapper.Success -> {
                            Log.i(TAG, "onViewCreated online ${currencies.data}: ")

                            currencies.data?.currencyData?.keys?.forEach {
                                mCurrencyList.add(it)
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
        }

    /* if the device will not have internet connection,
    load list of currencies that are being stored in database,
    so user could make an conversion, but with old rates */
    private val mOfflineListOfCurrencies: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.currencyData.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            currency.data?.forEach { curr ->
                                mOfflineCurrencyList.add(curr.baseCurrency)
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
        }

    /* track network state. If the database doesn't contain any currency data,
        and there's no internet connection, display TextView with explanation
        if program will detect that internet connection is present, then stop offline currencies coroutine
        and start online coroutine to display every currency that is present in the api. */
    private val mNetworkStateCoroutine: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.networkState.collect { state ->
                    when (state) {
                        is DataWrapper.Success -> {
                            mNetworkState = state.data?.name.toString()
                            Log.i(TAG, "onViewCreated $mNetworkState: ")

                            if (mNetworkState != "Available" && mIsDbInit) {
                                mBinding.conversionError.visibility = View.VISIBLE
                            }

                            if (mNetworkState == "Available") {
                                mOfflineListOfCurrencies.cancel()
                                mOnlineListOfCurrencies.start()
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
        }

    // retrieve base currency from the database.
    private val baseCurrency: Job
        get() = viewLifecycleOwner.lifecycleScope.launch {
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
                            mIsDbInit = state.data!!
                            baseCurrency.start()
                            mNetworkStateCoroutine.start()
                            mOfflineListOfCurrencies.start()
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

        /* Retrieve provided amount in EditText, and make an api call, based on:
           base currency, desired currency, and amount */
        mBinding.conversionConverseBtn.setOnClickListener {
            val defaultCurrencies = mBaseCurrency == "default" || mDesiredCurrency == "default"
            val isConversionEmpty = mBinding.conversionEnterValue.text.toString().isEmpty()

            if (defaultCurrencies || isConversionEmpty || mBinding.conversionEnterValue.text.toString()
                    .toInt() < 1
            ) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.select_desired_currency),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                mAmountToConversion = mBinding.conversionEnterValue.text.toString()

                if (mNetworkState == "Available") {
                    mOnlineConversion.start()
                } else {
                    mOfflineConversion.start()
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
//TODO make an code review in fragments. Seems like spinners are not reacting as they should after changing base currency

        // Refresh layout UI
        mBinding.conversionRefreshContainer.setOnRefreshListener {
            defaultViewsSetup()
            mBinding.conversionRefreshContainer.isRefreshing = false
        }
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
                    if (isTouched) {
                        isTouched = false
                        mDesiredCurrency = currencyList[position]
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

    /* Prepare layout to state as it was when user first entered it. Clear currency lists,
       and start one more time appropriate coroutines */
    private fun defaultViewsSetup() {
        mBinding.conversionEnterValue.text.clear()
        mBinding.conversionConvertedData.text = ""
        mBinding.conversionConvertedData.visibility = View.INVISIBLE
        mBinding.conversionError.visibility = View.INVISIBLE

        mBinding.conversionToTv.text = getString(R.string.currency_name)
        mBinding.conversionFromTv.text =
            String.format(getString(R.string.formatted_from), mBaseCurrency)

        mOfflineCurrencyList.clear()
        mCurrencyList.clear()

        mDesiredCurrency = "default"
        mAmountToConversion = ""

        mNetworkStateCoroutine.start()
        mOfflineListOfCurrencies.start()

    }
}
