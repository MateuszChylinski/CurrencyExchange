package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.HistoricalAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import java.text.SimpleDateFormat
import java.util.*

class HistoricalRates : Fragment() {
    //    VARIABLES
    private val TAG = "HistoricalRates"
    private var mDate: String = "def"
    private var mBaseCurrency = "default"

    private var mApiService: ApiServices = ApiServices.getInstance()

    private var mHistoricalAdapter: HistoricalAdapter? = null
    private var mIsChecked = false
    private var mCheckedCurrencies: SparseBooleanArray? = null
    private var mCurrenciesNames: MutableList<CurrencyNamesModel> = mutableListOf()
    private val mMinCall = Calendar.getInstance()
    private val mMaxCall = Calendar.getInstance()
    private var mIsInit = false
    private var mIsTouched = false
    private var mConcatenatedSymbols: String = ""

    //TOOLBAR
    private var mChangeBaseCurrency: ImageView? = null
    //    VIEWS
    private var mInfo: TextView? = null
    private var mSelectionInfo: TextView? = null
    private var mChangeInfo: TextView? = null
    private var mBaseCurrencyTV: TextView? = null
    private var mDateTV: TextView? = null

    private var mSaveDate: Button? = null
    private var mSaveSymbols: Button? = null

    private var mDatePicker: DatePicker? = null
    private var mSymbols: ListView? = null
    private var mChangeBase: Spinner? = null
    private var mSymbolsRv: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_historical_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mInfo = view.findViewById(R.id.historical_info)
        mSelectionInfo = view.findViewById(R.id.historical_select_info)
        mChangeInfo = view.findViewById(R.id.historical_change_info)
        mBaseCurrencyTV = view.findViewById(R.id.historical_base_tv)
        mDateTV = view.findViewById(R.id.historical_date_tv)
        mSaveDate = view.findViewById(R.id.historical_save_date)
        mSaveSymbols = view.findViewById(R.id.historical_save_symbols)
        mDatePicker = view.findViewById(R.id.historical_dt)
        mSymbols = view.findViewById(R.id.historical_symbols_lv)
        mChangeBase = view.findViewById(R.id.historical_change_base)
        mSymbolsRv = view.findViewById(R.id.historical_rv)

        mMinCall.set(1999, 2, 1)
        mDatePicker?.minDate = mMinCall.timeInMillis
        mDatePicker?.maxDate = mMaxCall.timeInMillis
        mChangeBaseCurrency = view.findViewById(R.id.historical_change_base_currency)
        mChangeBaseCurrency?.setOnClickListener{
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))

        }

        mSaveDate?.setOnClickListener {
            getDate()
            setVisibilityToLv()
        }
        mSaveSymbols?.setOnClickListener {
            getCurrencies()
        }
    }

    //  Get picked date by user in DatePicker view, and store it in mDate variable in format of 'yyyy-mm-dd'
    private fun getDate() {
//      setup date picker. Adjust min/max date to call

        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        cal.set(Calendar.YEAR, mDatePicker!!.year)
        cal.set(Calendar.MONTH, mDatePicker!!.month)
        cal.set(Calendar.DATE, mDatePicker!!.dayOfMonth)
        mDate = sdf.format(cal.time).toString()
    }

    //  Make unneeded views gone, prepare views for ListView
    private fun setVisibilityToLv() {
        mInfo?.visibility = View.GONE
        mDatePicker?.visibility = View.GONE
        mSaveDate?.visibility = View.GONE

        mSelectionInfo?.visibility = View.VISIBLE
        mSymbols?.visibility = View.VISIBLE
        mSaveSymbols?.visibility = View.VISIBLE
        mChangeInfo?.visibility = View.VISIBLE
        mChangeBase?.visibility = View.VISIBLE
        mBaseCurrencyTV?.visibility = View.VISIBLE
        mDateTV?.visibility = View.VISIBLE
        mDateTV?.text = String.format("Date: %s", mDate)

//        getCurrenciesFromDB()
    }


//    private fun getCurrenciesFromDB() {
//        mDatabaseViewModel.baseCurrency.observe(
//            requireActivity(),
//            androidx.lifecycle.Observer {
//                mBaseCurrency = it.toString()
//                mBaseCurrencyTV?.text = String.format("Base currency: %s", it.toString())
//            })
//        mDatabaseViewModel.currencyNames.observe(requireActivity(), androidx.lifecycle.Observer {
////          The reason why we're adding here 'it' to the 'mCurrenciesNames' is because in 'ListView' we're gonna need additional record, called 'All currencies' - since we shouldn't add it to the json response, it's better to just add response to the mutable list for the 'ListView'
//            mCurrenciesNames.addAll(it)
//            if (mBaseCurrency != "default") {
//                deleteBaseFromTheList(it as MutableList<CurrencyNamesModel>)
//            }
//        })
//    }

    private fun deleteBaseFromTheList(list: MutableList<CurrencyNamesModel>) {
        if (list.toString().contains(mBaseCurrency)) {
            val index = list.indices.find { list[it].toString() == mBaseCurrency }
            list.removeAt(index!!)
        }

        if (!mIsInit) {
            Log.i(TAG, "deleteBaseFromTheList: initial")
            setupSpinner(list)
            setupListView(list)
            mIsInit = true
        }
    }

    private fun setupSpinner(currencies: MutableList<CurrencyNamesModel>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencies)
        mChangeBase?.adapter = adapter
        mChangeBase?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (mIsTouched) {
                    Log.i(TAG, "onItemSelected: SPINNER " + currencies[p2])

                    mBaseCurrency = currencies[p2].toString()
                    mBaseCurrencyTV?.text = String.format("Base currency: %s", mBaseCurrency)
                    currencies.clear()
                    currencies.addAll(mCurrenciesNames)
                    deleteBaseFromTheList(currencies)
                    setupListView(currencies)
                    adapter.notifyDataSetChanged()
                } else {
                    mIsTouched = true
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "onNothingSelected: SPINNER ")
            }
        }
    }

    private fun setupListView(currencies: MutableList<CurrencyNamesModel>) {
//      copy list from the function arguments, so we can add 'All currencies' without worrying about spinner list. Otherwise, if we didn't do it, spinner list will also have a record wil 'All currencies'.
        val copiedCurrenciesList: MutableList<CurrencyNamesModel> = mutableListOf()
        copiedCurrenciesList.addAll(currencies)

        if (copiedCurrenciesList[0].toString() != "All currencies") {
            val curr = CurrencyNamesModel("All currencies")
            copiedCurrenciesList.add(0, curr)
        }
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_list_item_multiple_choice,
            copiedCurrenciesList
        )
        mSymbols?.adapter = adapter
        mSymbols?.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mSymbols?.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if (mSymbols?.isItemChecked(0) == true) {
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
//                Insert to the "mCheckedCurrencies" every currency name that user checked
                mCheckedCurrencies = mSymbols?.checkedItemPositions
            }
        }
    }

    fun checkEveryCurrency(state: Int) {
//      If state = 1, then user want to have checked all of the currencies. Loop over the ListView, and set every currency to be checked.
//      If state = 2, then loop over ListView, and uncheck every currency.
        if (state == 1) {
            for (i in 1..(mSymbols?.adapter?.count!!)) {
                mSymbols?.setItemChecked(i, true)
            }
        } else {
            for (i in 0..(mSymbols?.adapter?.count!!)) {
                mSymbols?.setItemChecked(i, false)
            }
        }
    }

    fun getCurrencies() {
//      After clicking on save button, this fun will be called. Then, program calculate how many items are in listview
//      Program start from the 1 index, because the 0 is All currencies, and program don't need in case it need to get all checked symbols
        for (i in 1..(mSymbols?.adapter?.count!!) - 1) {
            if (mCheckedCurrencies?.get(i)!!) {
                mConcatenatedSymbols += mSymbols?.adapter?.getItem(i).toString() + ", "
            }
        }
        prepareViewsForRecyclerView()
    }

    //  Prepare views to display RecyclerView. Delete unneeded views
    private fun prepareViewsForRecyclerView() {
        mSelectionInfo?.visibility = View.GONE
        mSymbols?.visibility = View.GONE
        mSaveSymbols?.visibility = View.GONE
        mChangeInfo?.visibility = View.GONE
        mChangeBase?.visibility = View.GONE

        mDateTV?.visibility = View.VISIBLE
        mSymbolsRv?.visibility = View.VISIBLE

//        getDataFromViewModel()
    }

    // Make a call within api to receive data about historical rates for selected base currency, and all of the currencies selected in ListView
//    private fun getDataFromViewModel() {
//        mRetrofitViewModel = ViewModelProvider(
//            this, CurrencyViewModelFactory(
//                CurrencyRetrofitRepository(mApiService)
//            )
//        ).get(CurrencyRetrofitViewModel::class.java)
//        mRetrofitViewModel.historicalRates(mDate, mConcatenatedSymbols, mBaseCurrency)
//        mRetrofitViewModel.historicalRates.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//            prepareRecyclerView(it.abc.toSortedMap())
//        })
//    }

    //   Make a basic basic setup of the RecyclerView
    private fun prepareRecyclerView(currencyData: SortedMap<String, Double>) {
        mHistoricalAdapter = HistoricalAdapter()
        mHistoricalAdapter?.setData(currencyData)
        mSymbolsRv?.layoutManager = LinearLayoutManager(this.context)
        mSymbolsRv?.adapter = mHistoricalAdapter
    }
}

