package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.MainActivity
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import java.text.SimpleDateFormat
import java.util.*


class Fluctuation : Fragment() {
    private val TAG = "Fluctuation"
    val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }

    // VARIABLES
//    private var testGlobalCurr: String = ""
    private var mBaseCurrency: String = ""
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private var mIsTouched = false
    private var mIsBaseChanged = false
    private lateinit var mSpinnerAdapter: ArrayAdapter<CurrencyNamesModel>


    private val currentCal = Calendar.getInstance()
    private val minimalCal = Calendar.getInstance()
    private var mToFullDate: String = "default"
    private var mFromFullDate: String = "default"


    private val mRetrofitServices = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel

    private var mCurrenciesForCallback: MutableList<String> = mutableListOf()
    private val mCurrenciesNames: MutableList<String> = mutableListOf()
    private val mCurrenciesStartRate: MutableList<Double> = mutableListOf()
    private val mCurrenciesEndRate: MutableList<Double> = mutableListOf()
    private val mCurrenciesChange: MutableList<Double> = mutableListOf()
    private val mCurrenciesChangePct: MutableList<Double> = mutableListOf()

    private var mCheckedSymbols: SparseBooleanArray? = null
    private var mIsChecked = false
    private var mConcatenatedSymbols: String = ""
    private val mCurrencyNamesFromVM: MutableList<CurrencyNamesModel> = mutableListOf()

    // VIEWS

    private var mChangeBaseCurrencyTV: TextView? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mSelectSymbolsTV: TextView? = null
    private var selectCurrToCallback: TextView? = null
    private var mRecyclerView: RecyclerView? = null

    var baseCurrencyTV: TextView? = null

    //    var fluctuationTV: TextView? = null
    var selectBaseCurrency: Spinner? = null
    var saveSymbols: Button? = null


    //  FROM
    var mCurrenciesListView: ListView? = null

    var fromDateTV: TextView? = null
    var fromCenterTV: TextView? = null
    var fromDatePicker: DatePicker? = null
    var fromOk: Button? = null

    //  TO
    var toDateTV: TextView? = null
    var toCenterTV: TextView? = null
    var toDatePicker: DatePicker? = null
    var toOk: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fluctuation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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


        mChangeBaseCurrencyTV = view.findViewById(R.id.fluctuation_fluctuation)
        mBaseCurrencyTV = view.findViewById(R.id.fluctuation_base_currency_tv)

        setupDatePicker()

        fromOk?.setOnClickListener {
            getDateFromUser(1)
            setupViewsToGetDate()
        }
        toOk?.setOnClickListener {
            getDateFromUser(2)
            setupViewsForListView()
        }
        saveSymbols?.setOnClickListener {
            getCurrencies()
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
//      1 = from / 2 = to
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

        fromDateTV?.text = String.format("From: %s", mFromFullDate)
        toDateTV?.text = String.format("To: %s", mToFullDate)

        retrieveCurrencyNamesForSpinner()

    }

    //  Retrieve currency names from database. Pass data to the 'setupBaseCurrencySpinner'
    private fun retrieveCurrencyNamesForSpinner() {
        mDatabaseViewModel.baseCurrency.observe(requireActivity(), androidx.lifecycle.Observer {
            mBaseCurrency = it.baseCurrency
            mBaseCurrencyTV?.text = String.format("Base Currency: %s", mBaseCurrency)
        })
        mDatabaseViewModel.allCurrencies.observe(requireActivity(), androidx.lifecycle.Observer {
            mCurrencyNamesFromVM.addAll(it as MutableList)

            if (mCurrencyNamesFromVM.size > 1) {
                deleteBaseCurrencyFromList(mBaseCurrency)
            }
        })
    }
    //  This function will delete base currency from list that will be forwarded to 'setupBaseCurrencySpinner' to populate spinner
    private fun deleteBaseCurrencyFromList(baseCurr: String) {
        val currenciesWithoutBase: MutableList<CurrencyNamesModel> = mCurrencyNamesFromVM.toMutableList()
        for (i in 1 until currenciesWithoutBase.size - 1) {
            if (currenciesWithoutBase[i].toString() == baseCurr) {
                currenciesWithoutBase.removeAt(i)
                setupBaseCurrencySpinner(currenciesWithoutBase)
            }
        }
    }

    private fun setupBaseCurrencySpinner(currencyNames: MutableList<CurrencyNamesModel>) {

        mSpinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        selectBaseCurrency?.adapter = mSpinnerAdapter
        selectBaseCurrency?.setSelection(0, false)
        Log.i(TAG, "setupBaseCurrencySpinner: $currencyNames")
        selectBaseCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemSelected: CLICKED "+mCurrencyNamesFromVM[p2])
                mBaseCurrency = mCurrencyNamesFromVM[p2].toString()
                mBaseCurrencyTV?.text = String.format("Base Currency: %s", mBaseCurrency)

                currencyNames.clear()
                currencyNames.addAll(mCurrencyNamesFromVM.toMutableList())


                for (i in 0 until currencyNames.size - 1) {
                    if (currencyNames[i].toString() == mBaseCurrency) {
                        currencyNames.removeAt(i)
                        mIsBaseChanged = true
                        setupListView(currencyNames)
                        mSpinnerAdapter.notifyDataSetChanged()
                    }
                }
                Log.i(TAG, "onItemSelected: after loop $currencyNames")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ")
            }
        }
        if (!mIsBaseChanged) {
            setupListView(currencyNames)
        }
    }

    private fun setupListView(list: MutableList<CurrencyNamesModel>) {

//      Add "All currencies" at top of the list in case when user would like to display every available currency from the api
        if (!list[0].equals("All currencies")) {
            val allCurrencies = CurrencyNamesModel("All currencies")
            list.add(0, allCurrencies)
        }
        val adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_list_item_multiple_choice,
            list
        )
        mCurrenciesListView?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mCurrenciesListView?.adapter = adapter
        mCurrenciesListView?.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                /**
                Whenever user will check some currency name, it will be added to the "mBolo" with id of clicked currency.
                Then, I can call the fun 'checkCurrency'
                 */

//              Checking if the "All currencies" is checked.
                if (mCurrenciesListView?.isItemChecked(0) == true) {
//                    If yes, check if program already checked once all currencies, if yes, just let user decide whether  he want to uncheck some currencies, or just uncheck everything
                    if (!mIsChecked) {
                        checkEveryCurrency(1)
                        mIsChecked = true
                    }
//                 Called whenever user uncheck the "All currencies". Uncheck every element in the ListView
                } else {
                    if (mIsChecked) {
                        checkEveryCurrency(2)
                        mIsChecked = false
                    }
                }
//              Insert to the "mBolo" every currency name that user checked
                mCheckedSymbols = mCurrenciesListView?.checkedItemPositions
            }
        }
    }

    fun getCurrencies() {
//      After clicking on save button, this fun will be called. Then, program calculate how many items are in listview
//      Program start from the 1 index, because the 0 is All currencies, and program don't need in case it need to get all checked symbols
        for (i in 1..(mCurrenciesListView?.adapter?.count!!) - 1) {
            if (mCheckedSymbols?.get(i)!!) {
                mCurrenciesForCallback.add(mCurrenciesListView?.adapter?.getItem(i).toString())
            }
        }
        prepareRecyclerView()
    }


    fun checkEveryCurrency(state: Int) {
//      If state = 1, then user want to have checked all of the currencies. Loop over the ListView, and set every currency to be checked.
//      If state = 2, then loop over ListView, and uncheck every currency.
        if (state == 1) {
            for (i in 1..(mCurrenciesListView?.adapter?.count!!)) {
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
        mRecyclerView?.visibility = View.VISIBLE

        baseCurrencyTV?.text = mBaseCurrency
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


        getFluctuationData()
    }


    private fun getFluctuationData() {
        mViewModel = ViewModelProvider(
            this,
            CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitServices))
        ).get(CurrencyRetrofitViewModel::class.java)


//      Iterate through every checked currency, and make a single string for callback by concatenation
        for (i in 0..mCurrenciesForCallback.size - 1) {
            mConcatenatedSymbols += mCurrenciesForCallback[i] + ","
        }

        mViewModel.fetchFluctuation(
            mFromFullDate,
            mToFullDate,
            mBaseCurrency,
            mConcatenatedSymbols
        )
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
//TODO - why does spinner display "All currencies?
//TODO - add comments to funs