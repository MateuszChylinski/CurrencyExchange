package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory
import kotlin.math.log
import com.example.currencyexchange.Fragments.Conversion as Conversion

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
    private var mChangeBaseIcon: ImageView? = null

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
    private var mIsFromInit = true
    private var mIsToInit = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_conversion, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: $mIsToInit | $mIsFromInit")
        retrieveCurrency()

        mFromTV = view.findViewById(R.id.conversion_from_tv)
        mToTV = view.findViewById(R.id.conversion_to_tv)
        mFromSpinner = view.findViewById(R.id.conversion_from_spinner)
        mToSpinner = view.findViewById(R.id.conversion_to_spinner)
        mGetValue = view.findViewById(R.id.conversion_enter_value)
        mConvertBtn = view.findViewById(R.id.conversion_converse_btn)
        mConvertedData = view.findViewById(R.id.conversion_converted_data)
        mChangeBaseIcon = view.findViewById(R.id.conversion_change_base_currency)
        mChangeBaseIcon?.setOnClickListener {
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))

        }
        mConvertBtn?.setOnClickListener {
            getValuesFromEditTexts()
        }
    }

    //  Retrieve data from database, by using the ViewModel
    private fun retrieveCurrency() {
        mDatabaseViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
            mBaseCurrency = it.toString()
            mFromTV?.text = String.format("From: %s", mBaseCurrency)
        })

        mDatabaseViewModel.currencyNames.observe(viewLifecycleOwner, Observer {
            if (it != null) {
// TODO               do i need it?
                mAllCurrencies.addAll(it)
                if (mBaseCurrency != "def") {
                    deleteBaseFromSpinner(mBaseCurrency, it as MutableList<CurrencyNamesModel>)
                }
            }
        })
    }


    private fun deleteBaseFromSpinner(
        whichSpinner: String,
        list: MutableList<CurrencyNamesModel>
    ) {
        if (mIsFromInit && mIsFromInit) {
            mIsFromInit = false
            mIsToInit = false
            Log.i(TAG, "deleteBaseFromSpinner: " + list.size)
            prepareFromSpinner(list)
            prepareToSpinner(list)
        } else {
            when (whichSpinner){
                "from" -> {
                    Log.i(TAG, "deleteBaseFromSpinner: CALLIJNG FFROM")
                    val index = list.indices.find {list[it].toString() == mBaseCurrency}
                    list.removeAt(index!!)

                    if (mDesiredCurrency != "def"){
                        val desiredIndex = list.indices.find {list[it].toString() == mDesiredCurrency}
                        list.removeAt(desiredIndex!!)
                    }

                    prepareFromSpinner(list)
                }
                "to" -> {
                    Log.i(TAG, "deleteBaseFromSpinner: CALLING TO $mBaseCurrency $mDesiredCurrency")
                    val baseIndex = list.indices.find {list[it].toString() == mDesiredCurrency}
                    list.removeAt(baseIndex!!)

//                    if (mBaseCurrency != "def"){
//                        Log.i(TAG, "deleteBaseFromSpinner: $mBaseCurrency")
//                        val desiredIndex = list.indices.find {list[it].toString() == mBaseCurrency}
//                    }

                    prepareToSpinner(list)
                }
            }
        }

    }

    //  Prepare "from" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareFromSpinner(currencyNames: MutableList<CurrencyNamesModel>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mFromSpinner?.adapter = adapter
        mFromSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (mIsFromTouched) {
                        Log.i(TAG, "onItemSelected: SELECTED FROM")

                        mBaseCurrency = currencyNames[p2].toString()
                        mFromTV?.text = String.format("From: %s", mBaseCurrency)
                        mIsFromTouched = false


//                      Since everytime when user will select any currency, it will be deleted from the list, so user could not refer again to previous currency. This is why there's a need to create a copy of list that stores every currency.
                        currencyNames.clear()
                        currencyNames.addAll(mAllCurrencies)

                        deleteBaseFromSpinner("from", currencyNames)
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

    //      Prepare "to" spinner. Init adapter, fetch spinner with data from ViewModel, implement OnItemClickListener
    private fun prepareToSpinner(currencyNames: MutableList<CurrencyNamesModel>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mToSpinner?.adapter = adapter
        mToSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (mIsToTouched) {

                        Log.i(TAG, "onItemSelected: SELECTED TO")

                        mDesiredCurrency = currencyNames[p2].toString()
                        mToTV?.text = String.format("To: %s", mDesiredCurrency)
                        mIsToTouched = false

//                      Since everytime when user will select any currency, it will be deleted from the list, so user could not refer again to previous currency. This is why there's a need to create a copy of list that stores every currency.
                        currencyNames.clear()
                        currencyNames.addAll(mAllCurrencies)
                        deleteBaseFromSpinner("to", currencyNames)
                        adapter.notifyDataSetChanged()

                    } else {
                        mIsToTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: ?")
                }
            }
    }

    //  Retrieve value from EditText, make converted data TextView visible, move forward to 'prepareConvertCall()'
    private fun getValuesFromEditTexts() {
        if (mBaseCurrency != "def" && mDesiredCurrency != "def") {
            mValue = mGetValue?.text.toString()
            mConvertedData?.visibility = View.VISIBLE
            prepareConvertCall()
        }else{
            Toast.makeText(requireActivity(), "You have to select desired currency in order to make a conversion!", Toast.LENGTH_LONG).show()
        }
    }

    //  Initiate the ViewModel, get data from repository, format result, and display it in the TextView
    private fun prepareConvertCall() {

        mCurrencyRetrofitViewModel = ViewModelProvider(
            this, CurrencyViewModelFactory(
                CurrencyRetrofitRepository(mRetrofitService)
            )
        ).get(CurrencyRetrofitViewModel::class.java)
        mCurrencyRetrofitViewModel.convertCurrency(mBaseCurrency, mDesiredCurrency, mValue)
        Log.i(TAG, "prepareConvertCall: $mBaseCurrency $mDesiredCurrency $mValue")
        mCurrencyRetrofitViewModel.convertCurrencyData.observe(viewLifecycleOwner, Observer {
            Log.i(TAG, "prepareConvertCall: ${it.result} ${it.success}")
            mConvertedValue = it.result
            mConvertedData?.text =
                String.format("You will receive: %.2f %s", mConvertedValue, mDesiredCurrency)
        })
    }
}
