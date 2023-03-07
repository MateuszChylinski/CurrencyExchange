package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentConversionBinding

class Conversion : Fragment() {
    private val TAG = "Conversion"

    private var mConversionBinding: FragmentConversionBinding? = null
    private val mBinding get() = mConversionBinding!!

    private lateinit var mViewModel: ConversionViewModel
    private var mApiInstance = ApiServices.getInstance()
    private var mDatabaseInstance: CurrencyDatabaseRepository? = null

    private var mBaseCurrency: String = "default"
    private var mDesiredCurrency: String = "default"
    private var mIsRefreshed: Boolean = false

    private var mCurrencyList: MutableList<CurrencyNamesModel> = mutableListOf()
    private var mAllCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {

//        mConversionBinding = FragmentConversionBinding.inflate(layoutInflater)
//        val view = mBinding.root
//
//        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
//        mViewModel = ViewModelProvider(
//            this,
//            ConversionFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
//        )[ConversionViewModel::class.java]
//
//<<<<<<< Updated upstream
//        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
//            mBaseCurrency = it
//            mBinding.conversionFromTv.text =
//                String.format(getString(R.string.formatted_from), mBaseCurrency)
//        })
//        /** The reason why I'm creating two lists with all of the currencies,
//         *  is because the 'mCurrencyList' is will NOT contain base currency, and desired currency.
//         *  In other hand, the 'mAllCurrencies' are not changed while program is running,
//         *  so it can provide new list of currencies to the 'mCurrencyList'*/
//        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
//            mCurrencyList.addAll(it)
//            mAllCurrencies.addAll(it)
//            prepareFromSpinner(mCurrencyList)
//            prepareToSpinner(mCurrencyList)
//        })
//=======
////        /** Observe base currency from ViewModel. */
////        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
////            mBaseCurrency = it
////            mBinding.conversionFromTv.text =
////                String.format(getString(R.string.formatted_from), mBaseCurrency)
////        })
////
////        /** Observe all available currencies, and pass them to the 'mCurrencyList'.  */
////        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
////            if (mCurrencyList.isEmpty()) {
////                mCurrencyList.addAll(it)
////            }
////            deleteBaseFromSpinner(mCurrencyList)
////        })
//        /**  Observe response from the api call, and display it in TextView.    */
//>>>>>>> Stashed changes
//        mViewModel.conversionResult.observe(viewLifecycleOwner, Observer {
//            mBinding.conversionConvertedData.visibility = View.VISIBLE
//            mBinding.conversionConvertedData.text = String.format(
//                getString(R.string.formatted_you_will_receive),
//                it?.result, it?.query?.to
//            )
//        })
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        mBinding.conversionChangeBaseCurrency.setOnClickListener {
//            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
//            findNavController().navigate(R.id.action_from_base_to_change)
//        }
//<<<<<<< Updated upstream
//        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
//            getValueFromEditText()
//        })
//
//        // Refresh layout
//        mBinding.conversionRefreshContainer.setOnRefreshListener {
//            // Clear variables
//            mCurrencyList.clear()
//            mCurrencyList.addAll(mAllCurrencies)
//            mDesiredCurrency = String()
//
//            mIsRefreshed = true
//            mBaseCurrency = mViewModel.getBaseCurrency()
//            defaultViewsSetup()
//            mBinding.conversionRefreshContainer.isRefreshing = false
//        }
//=======
//
//        /** Retrieve provided amount in EditText, and make an api call, based on:
//         *  base currency, desired currency, and amount*/
////        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
////            getValueFromEditText()
////        })
////
////        /** Refresh layout UI*/
////        mBinding.conversionRefreshContainer.setOnRefreshListener {
////            mDesiredCurrency = String()
////
////            mBaseCurrency = mViewModel.getBaseCurrency()
////            defaultViewsSetup(mCurrencyList)
////            mBinding.conversionRefreshContainer.isRefreshing = false
////        }
//>>>>>>> Stashed changes
//    }
//
//    /**  After refreshing the layout, clear text in convertedData TextView, and make it invisible.  */
//    private fun defaultViewsSetup() {
//        if (mIsRefreshed) {
//            mBinding.conversionEnterValue.text.clear()
//            mBinding.conversionConvertedData.text = ""
//            mBinding.conversionConvertedData.visibility = View.INVISIBLE
//
//            mBinding.conversionToTv.text = getString(R.string.currency_name)
//            mBinding.conversionFromTv.text =
//                String.format(getString(R.string.formatted_from), mBaseCurrency)
//            deleteBaseFromSpinner(mCurrencyList)
//        }
//    }
//
//    /** Check if given list contains base currency, and desired currency (if user have already picked one).
//     * If list contains these currencies, delete it, so user will not see them in spinner anymore.*/
//    private fun deleteBaseFromSpinner(
//        list: MutableList<CurrencyNamesModel>
//    ) {
//        if (mDesiredCurrency != "default") {
//            val desiredIndex = list.indices.find { list[it].toString() == mDesiredCurrency }
//            desiredIndex?.let { it -> list.removeAt(it) }
//        }
//        val baseIndex = list.indices.find { list[it].toString() == mBaseCurrency }
//        baseIndex?.let { it -> list.removeAt(it) }
//
//        prepareFromSpinner(list)
//        prepareToSpinner(list)
//    }
//
//    private fun prepareFromSpinner(currencyList: MutableList<CurrencyNamesModel>) {
//        var isTouched = false
//
//        val fromAdapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
//        mBinding.conversionFromSpinner.adapter = fromAdapter
//        mBinding.conversionFromSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (isTouched) {
//                        isTouched = false
//                        mBaseCurrency = currencyList[position].toString()
//                        mBinding.conversionFromTv.text =
//                            String.format(getString(R.string.formatted_from), mBaseCurrency)
//                        deleteBaseFromSpinner(currencyList)
//                    } else {
//                        isTouched = true
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Log.i(TAG, "onNothingSelected in 'from' spinner")
//                }
//            }
//    }
//
//    private fun prepareToSpinner(currencyList: MutableList<CurrencyNamesModel>) {
//        var isTouched = false
//        val toAdapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
//        mBinding.conversionToSpinner.adapter = toAdapter
//        mBinding.conversionToSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (isTouched) {
//                        isTouched = false
//                        mDesiredCurrency = currencyList[position].toString()
//                        mBinding.conversionToTv.text =
//                            String.format(getString(R.string.formatted_to), mDesiredCurrency)
//
//                        deleteBaseFromSpinner(currencyList)
//                    } else {
//                        isTouched = true
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
//                }
//            }
//    }
//
//    /** get given value from EditText, and if it contains any value, perform an api call, and fetch the data.
//     * if base/desired currency are empty, or the EditText does not contain any value, inform user to complete data*/
//<<<<<<< Updated upstream
//    private fun getValueFromEditText() {
//        val mAmount = mBinding.conversionEnterValue.text.toString()
//        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mAmount.isNotEmpty()) {
//            mViewModel.conversionCall(mBaseCurrency, mDesiredCurrency, mAmount)
//        } else {
//            Toast.makeText(
//                requireActivity(),
//                getString(R.string.select_desired_currency),
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//=======
////    private fun getValueFromEditText() {
////        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mBinding.conversionEnterValue.text.toString()
////                .isNotEmpty()
////        ) {
////            mViewModel.conversionCall(
////                mBaseCurrency,
////                mDesiredCurrency,
////                mBinding.conversionEnterValue.text.toString()
////            )
////        } else {
////            Toast.makeText(
////                requireActivity(),
////                getString(R.string.select_desired_currency),
////                Toast.LENGTH_LONG
////            ).show()
////        }
////    }
//>>>>>>> Stashed changes
}
