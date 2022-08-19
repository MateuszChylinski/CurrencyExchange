package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.core.util.keyIterator
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory
import kotlinx.coroutines.NonDisposableHandle.parent
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Fluctuation : Fragment() {
    var baseCurrencyTV: TextView? = null
    val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }

    //    TODO allocate views
    var fluctuationTV: TextView? = null
    var selectBaseCurrency: Spinner? = null


    //  FROM
    var mCurrenciesListView: ListView? = null

    var fromDateTV: TextView? = null
    var fromCenterTV: TextView? = null
    var fromDatePicker: DatePicker? = null
    var fromOk: Button? = null

    private var mFromFullDate: String = "default"

    //  TO
    var toDateTV: TextView? = null
    var toCenterTV: TextView? = null
    var toDatePicker: DatePicker? = null
    var toOk: Button? = null

    var saveSymbols: Button? = null

    private var mToFullDate: String = "default"


    private var mRecyclerView: RecyclerView? = null
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private val currentCal = Calendar.getInstance()
    private val minimalCal = Calendar.getInstance()

    private val mRetrofitServices = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel

    private val mCurrenciesNames: MutableList<String> = arrayListOf()
    private val mCurrenciesStartRate: MutableList<Double> = arrayListOf()
    private val mCurrenciesEndRate: MutableList<Double> = arrayListOf()
    private val mCurrenciesChange: MutableList<Double> = arrayListOf()
    private val mCurrenciesChangePct: MutableList<Double> = arrayListOf()


    private var testGlobalCurr: String = ""
    private var mBolo: SparseBooleanArray? = null
    private var isChecked = false
    private var mCurrenciesForCallback: MutableList<String> = arrayListOf()
    private var selectCurrToCallback: TextView? = null
    private var stringToTestConncatenation: String = ""

    //    NOW
    private var mBaseCurrency: String = ""
    private var mChangeBaseCurrencyTV: TextView? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mEnableEdit: Boolean = false
    private var mSelectSymbolsTV: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fluctuation, container, false)
    }
//  TODO - should I reset somehow the callback when user will pick another currency from spinner?
//  TODO - when RecyclerView is not filled, first currency occurs in almost middle of the screen. Is it because of the 'All currencies' in List?

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//      First part of views
        selectBaseCurrency = view.findViewById(R.id.fluctuation_select_base_currency)






        mCurrenciesListView = view.findViewById(R.id.fluctuation_select_symbols_lv)
        mRecyclerView = view.findViewById(R.id.fluctuation_rv)
        selectCurrToCallback = view.findViewById(R.id.fluctuation_select_curr_to_callback)
        saveSymbols = view.findViewById(R.id.fluctuation_save_symbols)

        fromDateTV = view.findViewById(R.id.fluctuation_from_date)
        fromCenterTV = view.findViewById(R.id.fluctuation_from_center_tv)
        fromDatePicker = view.findViewById(R.id.fluctuation_from_dt)
        fromOk = view.findViewById(R.id.fluctuation_set_from_ok)

        toDateTV = view.findViewById(R.id.fluctuation_to_date)
        toCenterTV = view.findViewById(R.id.fluctuation_to_center_tv)
        toDatePicker = view.findViewById(R.id.fluctuation_to_dt)
        toOk = view.findViewById(R.id.fluctuation_set_to_ok)
        mSelectSymbolsTV = view.findViewById(R.id.fluctuation_select_symbols_tv)


//        NOW
        mChangeBaseCurrencyTV = view.findViewById(R.id.fluctuation_fluctuation)
        mBaseCurrencyTV = view.findViewById(R.id.fluctuation_base_currency_tv)

        setupDatePicker()

        fromOk?.setOnClickListener {
            getDateFromUser(1)
            Log.i(TAG, "onViewCreated: TEST FROM DATE: $mFromFullDate")
            setupViewsToGetDate()
        }
        toOk?.setOnClickListener {
            getDateFromUser(2)
            Log.i(TAG, "onViewCreated: TEST TO DATE: $mToFullDate")
            setupViewsForListView()
        }
        saveSymbols?.setOnClickListener{
            getCurrencies()
            Log.i(TAG, "onViewCreated: $mCurrenciesForCallback")
        }
    }

    private fun setupDatePicker() {
//      minimal possible date to execute: 1999-02-01 YYYY/MM/DD.
        minimalCal.set(1999, 2, 1)
        fromDatePicker?.minDate = minimalCal.timeInMillis
        toDatePicker?.minDate = minimalCal.timeInMillis


        fromDatePicker?.maxDate = currentCal.timeInMillis
        toDatePicker?.maxDate = currentCal.timeInMillis
    }

    private fun getDateFromUser(selection: Int?) {
//      1 - from / 2 - to
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        when (selection) {
            1 -> {
                cal.set(Calendar.YEAR, fromDatePicker!!.year)
                cal.set(Calendar.MONTH, fromDatePicker!!.month)
                cal.set(Calendar.DAY_OF_MONTH, fromDatePicker!!.dayOfMonth)
                mFromFullDate = sdf.format(cal.time).toString()
            }
            2 -> {
                cal.set(Calendar.YEAR, toDatePicker!!.year)
                cal.set(Calendar.MONTH, toDatePicker!!.month)
                cal.set(Calendar.DAY_OF_MONTH, toDatePicker!!.dayOfMonth)
                mToFullDate = sdf.format(cal.time).toString()
            }
        }
    }

    private fun setupViewsToGetDate() {
        fromCenterTV?.visibility = View.INVISIBLE
        fromDatePicker?.visibility = View.INVISIBLE
        fromOk?.visibility = View.INVISIBLE

        toCenterTV?.visibility = View.VISIBLE
        toDatePicker?.visibility = View.VISIBLE
        toOk?.visibility = View.VISIBLE
    }

    private fun setupViewsForListView() {
        toCenterTV?.visibility = View.INVISIBLE
        toDatePicker?.visibility = View.INVISIBLE
        toOk?.visibility = View.INVISIBLE

        mChangeBaseCurrencyTV?.visibility = View.VISIBLE
        selectBaseCurrency?.visibility = View.VISIBLE
        mBaseCurrencyTV?.visibility = View.VISIBLE
        fromDateTV?.visibility = View.VISIBLE
        toDateTV?.visibility = View.VISIBLE
        mCurrenciesListView?.visibility = View.VISIBLE
        mSelectSymbolsTV?.visibility = View.VISIBLE
        saveSymbols?.visibility = View.VISIBLE

//      TODO add string formatting
        fromDateTV?.text = "From: " + mFromFullDate
        toDateTV?.text = "To: " + mToFullDate

        retrieveCurrencyNamesForSpinner()
    }

    //  Retrieve currency names from database. Pass data to the 'setupBaseCurrencySpinner'
    private fun retrieveCurrencyNamesForSpinner() {
        mDatabaseViewModel.fluctuationRatesForSpinner.observe(viewLifecycleOwner) {
            setupBaseCurrencySpinner(it as MutableList<CurrencyDatabaseModel>)
        }
        mDatabaseViewModel.allCurrencies.observe(viewLifecycleOwner) {
            setupListView(it as MutableList<CurrencyDatabaseModel>)
        }
    }

    //  Prepare 'selectBaseCurrency' spinner. Make an adapter, initiate list with currency names from database, initiate listener.
    private fun setupBaseCurrencySpinner(currencyNames: List<CurrencyDatabaseModel>) {

        val baseCurrencySpinnerAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item, currencyNames
        )

        selectBaseCurrency?.adapter = baseCurrencySpinnerAdapter
        selectBaseCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemClick: SELECTED " + selectBaseCurrency?.getItemAtPosition(p2))
                mBaseCurrency = selectBaseCurrency?.getItemAtPosition(p2).toString()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemClick: ")
            }
        }
    }

    private fun setupListView(list: MutableList<CurrencyDatabaseModel>) {

//      Add "All currencies" at top of the list in case when user would like to display every available currency from the api
        val allCurrencies = CurrencyDatabaseModel("All currencies")
        list.add(0, allCurrencies)

        val adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_list_item_multiple_choice,
            list
        )
        mCurrenciesListView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mCurrenciesListView?.adapter = adapter
        mCurrenciesListView?.onItemClickListener = object : AdapterView.OnItemSelectedListener,
            OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemSelected: ")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ")
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /**
                Whenever user will check some currency name, it will be added to the "mBolo" with id of clicked currency.
                Then, I can call the fun 'checkCurrency'
                 */

//              Checking if the "All currencies" is checked.
                if (mCurrenciesListView?.isItemChecked(0) == true) {
//                    If yes, check if program already checked once all currencies, if yes, just let user decide whether  he want to uncheck some currencies, or just uncheck everything
                    if (!isChecked) {
                        checkEveryCurrency(1)
                        isChecked = true
                    }
//                 Called whenever user uncheck the "All currencies". Uncheck every element in the ListView
                } else {
                    if (isChecked) {
                        checkEveryCurrency(2)
                        isChecked = false
                    }
                }
                mBolo = mCurrenciesListView?.checkedItemPositions
            }
        }
    }

    fun getCurrencies() {
        for (i in 1..(mCurrenciesListView?.adapter?.count!!) - 1)
            if (mBolo?.get(i)!!) {
                mCurrenciesForCallback.add(mCurrenciesListView?.adapter?.getItem(i).toString())
            }
        prepareRecyclerView()
    }


    fun checkEveryCurrency(state: Int) {
        if (state == 1) {
            for (i in 0..(mCurrenciesListView?.adapter?.count!!)) {
                mCurrenciesListView?.setItemChecked(i, true)
            }
        } else {
            for (i in 0..(mCurrenciesListView?.adapter?.count!!)) {
                mCurrenciesListView?.setItemChecked(i, false)
            }
        }
    }


    private fun prepareRecyclerView() {
        baseCurrencyTV?.visibility = View.VISIBLE
        baseCurrencyTV?.text = testGlobalCurr
        mRecyclerView?.layoutManager = LinearLayoutManager(this.requireContext())
        mFluctuationAdapter = FluctuationAdapter()

        mChangeBaseCurrencyTV?.visibility = View.INVISIBLE
        selectBaseCurrency?.visibility = View.INVISIBLE
        mBaseCurrencyTV?.visibility = View.INVISIBLE
        fromDateTV?.visibility = View.INVISIBLE
        toDateTV?.visibility = View.INVISIBLE
        mCurrenciesListView?.visibility = View.INVISIBLE
        mSelectSymbolsTV?.visibility = View.INVISIBLE
        saveSymbols?.visibility = View.INVISIBLE

        mRecyclerView?.visibility = View.VISIBLE

        getFluctuationData()
    }


    private fun getFluctuationData() {
        mViewModel = ViewModelProvider(
            this,
            CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitServices))
        ).get(CurrencyRetrofitViewModel::class.java)


//      Iterate through every checked currency, and make a single string for callback by concatenation
        for (i in 0..mCurrenciesForCallback.size - 1) {
            stringToTestConncatenation += mCurrenciesForCallback[i] + ","
        }
        Log.i(TAG, "getFluctuationData: test $stringToTestConncatenation")
        mViewModel.fetchFluctuation(mFromFullDate, mToFullDate, "PLN", stringToTestConncatenation)
        mViewModel.fluctuationRates.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            for (x in it.rates.keys) {
                mCurrenciesNames.add(x)
                mCurrenciesStartRate.add(it.rates.getValue(x).startRate)
                mCurrenciesEndRate.add(it.rates.getValue(x).endRate)
                mCurrenciesChange.add(it.rates.getValue(x).change)
                mCurrenciesChangePct.add(it.rates.getValue(x).change_pct)
            }
            mFluctuationAdapter?.setData(
                mCurrenciesNames,
                mCurrenciesStartRate,
                mCurrenciesEndRate,
                mCurrenciesChange,
                mCurrenciesChangePct
            )
            mRecyclerView?.adapter = mFluctuationAdapter
        })
    }
}