package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory

class Conversion : Fragment() {
    //  TAG
    private val TAG = "Conversion"

    //  Views
    private var mFromTV: TextView? = null
    private var mToTV: TextView? = null
    private var mFromSpinner: Spinner? = null
    private var mToSpinner: Spinner? = null
    private var mGetValue: EditText? = null
    private var mConvertBtn: Button? = null
    private var mConvertedData: TextView? = null

    //  Variables
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }

    private lateinit var mCurrencyRetrofitViewModel: CurrencyRetrofitViewModel
    private var mRetrofitService = ApiServices.getInstance()

    private var mBaseCurrency: String = "def"
    private var mDesiredCurrency: String = "def"
    private var mValue: String = "none"
    private var mConvertedValue: Double = 0.0
    private var mIsFromTouched = false
    private var mIsToTouched = false
    private var mAllCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()

    private var mIsFromInit = false
    private var mIsToInit = false

    private var mIsFromUpdated = false
    private var mIsToUpdated = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_conversion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFromTV = view.findViewById(R.id.conversion_from_tv)
        mToTV = view.findViewById(R.id.conversion_to_tv)
        mFromSpinner = view.findViewById(R.id.conversion_from_spinner)
        mToSpinner = view.findViewById(R.id.conversion_to_spinner)
        mGetValue = view.findViewById(R.id.conversion_enter_value)
        mConvertBtn = view.findViewById(R.id.conversion_converse_btn)
        mConvertedData = view.findViewById(R.id.conversion_converted_data)

        //        TODO turn it on when finished navigation comp

//        retrieveCurrency()

        mConvertBtn?.setOnClickListener {
//            //        TODO turn it on when finished navigation comp
//            getValuesFromEditTexts()
        }
    }

    //  Retrieve data from database, by using the ViewModel
    private fun retrieveCurrency() {

        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
            mBaseCurrency = it.toString()
            mFromTV?.text = String.format("From: %s", mBaseCurrency)

        })
        mDatabaseViewModel.currencyNames.observe(requireActivity(), Observer {
            if (it != null) {
                mAllCurrencies.addAll(it)
                if (mBaseCurrency != "def") {
                    deleteBaseFromSpinner(mBaseCurrency, it as MutableList<CurrencyNamesModel>)
                }
            }
        })
    }

    private fun deleteBaseFromSpinner(currency: String, list: MutableList<CurrencyNamesModel>) {
        //      Both of the currencies were changed
        if (mBaseCurrency != "def" && mDesiredCurrency != "def") {
            Log.i(TAG, "deleteBaseFromSpinner: BOTH")
            val indexBase = list.indices.find { list[it].toString() == mBaseCurrency }
            val indexDesired = list.indices.find { list[it].toString() == mDesiredCurrency }
            list.removeAt(indexBase!!)
            list.removeAt(indexDesired!!)
        }else{
            Log.i(TAG, "deleteBaseFromSpinner: single")
//          just one currency were changed??
            val index = list.indices.find { list[it].toString() == currency }
            list.removeAt(index!!)
        }

        if (!mIsFromInit || !mIsToInit) {
//        if (!mIsFromInit || !mIsToInit || mIsFromUpdated || mIsToUpdated) {
            prepareFromSpinner(list)
            prepareToSpinner(list)

            mIsFromInit = true
            mIsToInit = true

//            mIsFromUpdated = false
//            mIsToUpdated = false
        }
    }

    //  Prepare "from" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareFromSpinner(currencyNames: MutableList<CurrencyNamesModel>) {

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mFromSpinner?.adapter = adapter
        mFromSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (mIsFromTouched) {

                    mBaseCurrency = currencyNames[p2].toString()
                    mFromTV?.text = String.format("From: %s", mBaseCurrency)

                    currencyNames.clear()
                    currencyNames.addAll(mAllCurrencies)
                    deleteBaseFromSpinner(mBaseCurrency, currencyNames)
                    adapter.notifyDataSetChanged()

                } else {
                    mIsFromTouched = true
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ?")
            }
        }
    }

    //  Prepare "to" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareToSpinner(currencyNames: MutableList<CurrencyNamesModel>) {

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mToSpinner?.adapter = adapter
        mToSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (mIsToTouched) {

                    mDesiredCurrency = currencyNames[p2].toString()
                    mToTV?.text = String.format("To: %s", mDesiredCurrency)

                    currencyNames.clear()
                    currencyNames.addAll(mAllCurrencies)
                    deleteBaseFromSpinner(mDesiredCurrency, currencyNames)
                    adapter.notifyDataSetChanged()


                } else {
                    mIsToTouched = true
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ")
            }
        }
    }

    //  Retrieve value from EditText, make converted data TextView visible, move forward to 'prepareConvertCall()'
    private fun getValuesFromEditTexts() {
        mValue = mGetValue?.text.toString()
        mConvertedData?.visibility = View.VISIBLE
        prepareConvertCall()
    }

    //  Initiate the ViewModel, get data from repository, format result, and display it in the TextView
    private fun prepareConvertCall() {

        mCurrencyRetrofitViewModel = ViewModelProvider(
            this, CurrencyViewModelFactory(
                CurrencyRetrofitRepository(mRetrofitService)
            )
        ).get(CurrencyRetrofitViewModel::class.java)
        mCurrencyRetrofitViewModel.convertCurrency(mBaseCurrency, mDesiredCurrency, mValue)
        mCurrencyRetrofitViewModel.convertCurrencyData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "prepareConvertCall: ${it.result} ${it.success}")
            mConvertedValue = it.result
            mConvertedData?.text =
                String.format("You will receive: %.2f %s", mConvertedValue, mDesiredCurrency)
        })
    }
}
