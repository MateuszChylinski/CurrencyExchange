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
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentConversionBinding

class Conversion : Fragment() {
    private val TAG = "Conversion"

    private var mAmount: String = "none"
    private var mConversionBinding: FragmentConversionBinding? = null
    private val mBinding get() = mConversionBinding!!

    private lateinit var mViewModel: ConversionViewModel
    private var mApiInstance = ApiServices.getInstance()
    private var mDatabaseInstance: CurrencyDatabaseRepository? = null

    private var mBaseCurrency: String = "default"
    private var mDesiredCurrency: String = "default"
    private var mCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mConversionBinding = FragmentConversionBinding.inflate(layoutInflater)
        val view = mBinding.root

        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
        mViewModel = ViewModelProvider(
            this,
            ConversionFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
        )[ConversionViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.conversionChangeBaseCurrency.setOnClickListener {
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
        }
        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
            mBinding.conversionFromTv.text = String.format("From: %s", it)
            mBaseCurrency = it.toString()
            Log.i(TAG, "onViewCreate: $mBaseCurrency")

        })
        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
            mCurrencies.addAll(it)
            prepareFromSpinner(mCurrencies)
            prepareToSpinner(mCurrencies)
        })
        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
            getValueFromEditText()
        })
    }

    private fun deleteBaseFromSpinner(
        list: MutableList<CurrencyNamesModel>
    ) {
        if (mDesiredCurrency != "default") {
            val desiredIndex = list.indices.find { list[it].toString() == mDesiredCurrency }
            desiredIndex?.let { it -> list.removeAt(it) }
        }
        val baseIndex = list.indices.find { list[it].toString() == mBaseCurrency }
        baseIndex?.let { it -> list.removeAt(it) }

        prepareFromSpinner(list)
        prepareToSpinner(list)
    }

    private fun prepareFromSpinner(currencyList: MutableList<CurrencyNamesModel>) {
        var isTouched = false

        val fromAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
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
                        mBaseCurrency = currencyList[position].toString()
                        mBinding.conversionFromTv.text =
                            String.format("From: %s", mBaseCurrency)

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

    private fun prepareToSpinner(currencyList: MutableList<CurrencyNamesModel>) {
        var isTouched = false
        val toAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
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
                        mDesiredCurrency = currencyList[position].toString()
                        mBinding.conversionToTv.text =
                            String.format("To: %s", mDesiredCurrency)

                        deleteBaseFromSpinner(mCurrencies)
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
                }
            }
    }

    private fun getValueFromEditText() {
        mAmount = mBinding.conversionEnterValue.text.toString()

        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mAmount.isNotEmpty()) {
            mViewModel.conversionCall(mBaseCurrency, mDesiredCurrency, mAmount)
            mViewModel.conversionResult.observe(viewLifecycleOwner, Observer {

                mBinding.conversionConvertedData.visibility = View.VISIBLE
                mBinding.conversionConvertedData.text = String.format(
                    "You will receive %.2f %s",
                    it,
                    mDesiredCurrency
                )
            })
        } else {
            Toast.makeText(
                requireActivity(),
                "You have to select desired currency, and enter amount of currency that you want to exchange in order to make a conversion!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
