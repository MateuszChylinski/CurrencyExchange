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
import kotlinx.coroutines.launch

class Conversion : Fragment() {
    private val TAG = "Conversion"
    private var mBaseCurrency: String = "default"
    private var mDesiredCurrency: String = "default"
    private var mCurrencyList: MutableList<String> = mutableListOf()
    private var mConversionBinding: FragmentConversionBinding? = null
    private val mBinding get() = mConversionBinding!!
    private val mViewModel: ConversionViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mConversionBinding = FragmentConversionBinding.inflate(layoutInflater)
        val view = mBinding.root

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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.allCurrencies.collect { currencies ->
                    when (currencies) {
                        is DataWrapper.Success -> {
                            currencies.data?.currencyData?.keys?.forEach {
                                mCurrencyList.add(it)
                            }
                            prepareFromSpinner(mCurrencyList)
                            prepareToSpinner(mCurrencyList)
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
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Prepare a flag, to let ViewPager host fragment know, when it should navigate user to
         * 'ChangeBaseCurrency' fragment.   */
        mBinding.conversionChangeBaseCurrency.setOnClickListener {
            val mFragmentVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            mFragmentVM.setMoveFlag(true)
        }

        /** Retrieve provided amount in EditText, and make an api call, based on:
         *  base currency, desired currency, and amount*/
        mBinding.conversionConverseBtn.setOnClickListener {
            getValueFromEditText()
        }

        /** Refresh layout UI*/
        mBinding.conversionRefreshContainer.setOnRefreshListener {
            mDesiredCurrency = String()

            //TODO check lifecycle state after refresh?
//            mBaseCurrency = mViewModel.getBaseCurrency()
            defaultViewsSetup(mCurrencyList)
            mBinding.conversionRefreshContainer.isRefreshing = false
        }
    }

    /** Prepare 'from' spinner. This spinner allow to pick new, temporary base currency  */
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
                        deleteBaseFromSpinner(currencyList)
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected in 'from' spinner")
                }
            }
    }

    /** Prepare 'to' spinner. Picked currency from this spinner, will be marked as desired currency in api call later on*/
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

                        deleteBaseFromSpinner(currencyList)
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
                }
            }
    }

    /** Copy list of all currencies,
     *  check if given list contains base currency, and desired currency (if user have already picked one).
     *  If list contains these currencies, delete it, so user will not see them in spinner anymore.  */
    private fun deleteBaseFromSpinner(currencyList: MutableList<String>) {
        if (mDesiredCurrency != "default") {
            val desiredIndex =
                currencyList.indices.find { currencyList[it] == mDesiredCurrency }
            desiredIndex?.let { currencyList.removeAt(it) }
        }
        val baseIndex =
            currencyList.indices.find { currencyList[it] == mBaseCurrency }
        baseIndex?.let { currencyList.removeAt(it) }

        prepareFromSpinner(currencyList)
        prepareToSpinner(currencyList)
    }

    /** get given value from EditText, and if it contains any value, perform an api call, and fetch the data.
     * if base/desired currency are empty, or the EditText does not contain any value, inform user to complete data*/
    private fun getValueFromEditText() {
        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mBinding.conversionEnterValue.text.toString()
                .isNotEmpty()
        ) {
            mViewModel.exchangeCurrency(
                baseCurrency = mBaseCurrency,
                selectedCurrency = mDesiredCurrency,
                amount = mBinding.conversionEnterValue.text.toString()
            )
            //TODO TEST
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.exchangeState.observe(viewLifecycleOwner, Observer {
                    mBinding.conversionConvertedData.visibility = View.VISIBLE
                    mBinding.conversionConvertedData.text = String.format(
                        getString(
                            R.string.formatted_you_will_receive,
                            it.data?.result,
                            it?.data?.query?.to
                        )
                    )
                    Log.i(
                        TAG,
                        "getValueFromEditText: FROM ${it.data?.query?.from} TO ${it.data?.query?.to} AMOUNT ${it.data?.query?.amount} RESULT = ${it.data?.result}"
                    )
                })
            }
        } else {
            Toast.makeText(
                requireActivity(),
                getString(R.string.select_desired_currency),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /** After refreshing the layout, copy list of all currencies, delete base currency from the list,
     * clear TextView that display converted currency, make it invisible */
    private fun defaultViewsSetup(currencyList: MutableList<String>) {
        deleteBaseFromSpinner(currencyList)

        mBinding.conversionEnterValue.text.clear()
        mBinding.conversionConvertedData.text = ""
        mBinding.conversionConvertedData.visibility = View.INVISIBLE

        mBinding.conversionToTv.text = getString(R.string.currency_name)
        mBinding.conversionFromTv.text =
            String.format(getString(R.string.formatted_from), mBaseCurrency)
    }
}