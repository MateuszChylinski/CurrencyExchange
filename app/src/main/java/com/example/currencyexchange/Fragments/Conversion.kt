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
    private var mFromCurrencyName = "def"
    private var mToCurrencyName = "def"
    private var mValue: String = "none"
    private var mConvertedValue: Double = 0.0

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

        retrieveCurrency()

        mConvertBtn?.setOnClickListener {
            getValuesFromEditTexts()
        }
    }

    //  Retrieve data from database, by using the ViewModel
    private fun retrieveCurrency() {
        mDatabaseViewModel.allCurrencies.observe(requireActivity(), Observer {
            if (it != null) {
                prepareFromSpinner(it)
                prepareToSpinner(it)
            }
        })
        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
            mBaseCurrency = it.toString()
            mFromCurrencyName = mBaseCurrency
        })
    }

    //  Prepare "from" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareFromSpinner(currencyNames: List<CurrencyNamesModel>) {

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mFromSpinner?.adapter = adapter
        //      Avoid "getting" item clicked on fragment created/started

        mFromSpinner?.setSelection(0, false)
        mFromSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemSelected FROM SELECTED " + currencyNames[p2])
                mFromCurrencyName = currencyNames[p2].toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ?")
            }
        }
    }

    //  Prepare "to" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareToSpinner(currencyNames: List<CurrencyNamesModel>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mToSpinner?.adapter = adapter

//      Avoid "getting" item clicked on fragment created/started
        mToSpinner?.setSelection(0, false)
        mToSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemSelected TO SELECTED " + currencyNames[p2])
                mToCurrencyName = currencyNames[p2].toString()
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

        mCurrencyRetrofitViewModel = ViewModelProvider(this, CurrencyViewModelFactory(
            CurrencyRetrofitRepository(mRetrofitService))).get(CurrencyRetrofitViewModel::class.java)

        mCurrencyRetrofitViewModel.convertCurrency(mFromCurrencyName, mToCurrencyName, mValue)
        mCurrencyRetrofitViewModel.convertCurrencyData.observe(viewLifecycleOwner, Observer {
            mConvertedValue = it.result
            mConvertedData?.text = String.format("You will receive: %.2f %s",mConvertedValue, mToCurrencyName)
        })
    }
}
//TODO ADD POSSIBILITY TO MAKE ANOTHER CALL
