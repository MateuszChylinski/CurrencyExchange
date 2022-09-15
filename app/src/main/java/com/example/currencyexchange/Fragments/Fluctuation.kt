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
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class Fluctuation : Fragment() {
    private val TAG = "Fluctuation"
    val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }

    // VARIABLES
    private var mBaseCurrency: String = "default"
    private var mFluctuationAdapter: FluctuationAdapter? = null

    lateinit var mSpinnerAdapter: ArrayAdapter<CurrencyNamesModel>

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
    private var mIsSpinnerInit = false
    private var mIsTouched = false

    // VIEWS
    private var mChangeBaseCurrencyTV: TextView? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mSelectSymbolsTV: TextView? = null
    private var selectCurrToCallback: TextView? = null
    private var mRecyclerView: RecyclerView? = null

    private var baseCurrencyTV: TextView? = null
    private var selectBaseCurrency: Spinner? = null
    private var saveSymbols: Button? = null


    //  FROM
    private var mCurrenciesListView: ListView? = null

    private var fromDateTV: TextView? = null
    private var fromCenterTV: TextView? = null
    private var fromDatePicker: DatePicker? = null
    private var fromOk: Button? = null

    //  TO
    private var toDateTV: TextView? = null
    private var toCenterTV: TextView? = null
    private var toDatePicker: DatePicker? = null
    private var toOk: Button? = null

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

//        //        TODO turn it on when finished navigation comp
//        setupDatePicker()

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

    // Prepare DatePicker. Set min/max possible date to execute (from 1999-02-01, to present day) in YYYY/MM/DD format.
    private fun setupDatePicker() {
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
        fromCenterTV?.visibility = View.GONE
        fromDatePicker?.visibility = View.GONE
        fromOk?.visibility = View.GONE

        toCenterTV?.visibility = View.VISIBLE
        toDatePicker?.visibility = View.VISIBLE
        toOk?.visibility = View.VISIBLE
    }

    private fun setupViewsForListView() {
        toCenterTV?.visibility = View.GONE
        toDatePicker?.visibility = View.GONE
        toOk?.visibility = View.GONE

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
            mBaseCurrency = it
            mBaseCurrencyTV?.text = String.format("Base Currency: %s", mBaseCurrency)
        })
        mDatabaseViewModel.currencyNames.observe(
            requireActivity(),
            androidx.lifecycle.Observer {
                mCurrencyNamesFromVM.addAll(it as MutableList)
                if (mBaseCurrency != "default") {
                    deleteBaseCurrencyFromList(it as MutableList<CurrencyNamesModel>)
                }
            })
    }

    //  This function will delete base currency from list that will be forwarded to 'setupBaseCurrencySpinner' to populate spinner
    private fun deleteBaseCurrencyFromList(list: MutableList<CurrencyNamesModel>) {
        if (list.toString().contains(mBaseCurrency)) {
            val index = list.indices.find {
                list[it].toString() == mBaseCurrency
            }
            list.removeAt(index!!)
        }

        if (!mIsSpinnerInit) {
            setupBaseCurrencySpinner(list)
            setupListView(list)
            mIsSpinnerInit = true
        }
    }


    private fun setupBaseCurrencySpinner(currencyNames: MutableList<CurrencyNamesModel>) {

        mSpinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        selectBaseCurrency?.adapter = mSpinnerAdapter
        selectBaseCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (mIsTouched) {
                    mBaseCurrency = currencyNames[p2].toString()
                    mBaseCurrencyTV?.text = String.format("Base Currency: %s", mBaseCurrency)

                    currencyNames.clear()
                    currencyNames.addAll(mCurrencyNamesFromVM.toMutableList())
                    deleteBaseCurrencyFromList(currencyNames)
                    setupListView(currencyNames)
                    mSpinnerAdapter.notifyDataSetChanged()
                } else {
                    mIsTouched = true
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: ")
            }
        }
    }


    private fun setupListView(list: MutableList<CurrencyNamesModel>) {

//      Copy list list that came as argument for this function, because adding the 'All currencies' to list from the arguments will affect list that will be displayed in spinner, so we would have the 'All currencies' in the spinner as well.
        val copiedList: MutableList<CurrencyNamesModel> = mutableListOf()
        copiedList.addAll(list)
//      Ad the top of ListView add new record - all currencies so user can check it instead of checking every single one.
        val ac = CurrencyNamesModel("All currencies")
        copiedList.add(0, ac)


        val adapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_list_item_multiple_choice,
            copiedList
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

    // prepare an api call
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
