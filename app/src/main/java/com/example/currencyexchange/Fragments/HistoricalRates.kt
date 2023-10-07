package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.Adapters.HistoricalAdapter
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.FragmentTagViewModel
import com.example.currencyexchange.ViewModels.HistoricalViewModel
import com.example.currencyexchange.databinding.FragmentHistoricalRatesBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class HistoricalRates : Fragment() {
    private val TAG = "HistoricalRates"
    private var _binding: FragmentHistoricalRatesBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: HistoricalViewModel by activityViewModels()
    private val mCalendar = Calendar.getInstance()
    private var mHistoricalAdapter: HistoricalAdapter = HistoricalAdapter()

    private val mCurrencyList: MutableList<String> = mutableListOf()
    private val mPickedCurrencies: MutableList<String> = mutableListOf()

    private var mDate: String = "default"
    private var mBaseCurrency = "default"
    private var mIsInternetProvided = false

    /** Retrieve base currency from the database */
    private val baseCurr: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.baseCurrency.collect { currency ->
                when (currency) {
                    is DataWrapper.Success -> {
                        mBaseCurrency = currency.data?.baseCurrency.toString()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve base currency from the ViewModel. ${currency.message}",
                        )
                    }
                }
            }
        }

    /** Retrieve list of currencies from the database */
    private val allCurrenciesJob: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.allCurrencies.collect { currencies ->
                when (currencies) {
                    is DataWrapper.Success -> {
                        currencies.data?.currencyData?.keys?.forEach {
                            mCurrencyList.add(it)
                        }

                        /** Add to the currency list a value called 'Currency' as a row, since spinners by default are picking values when initiated.
                        By adding this "header", user will be able to pick first currency */

                        if (!mCurrencyList.contains("Select currency")) {
                            mCurrencyList.add(0, "Select currency")
                        }
                        deleteBaseFromTheList()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve all currencies from the ViewModel. ${currencies.message}",
                        )
                    }
                }
            }
        }

    /** Make an api call, and observe LiveData from ViewModel */
    private val mApiCall: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {

            mViewModel.fetchHistoricalData(
                baseCurrency = mBaseCurrency,
                selectedCurrencies = mPickedCurrencies.joinToString(separator = ", "),
                date = mDate
            )
            mObserveResponse.start()
        }

    private val mObserveResponse: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.historicalData.observe(viewLifecycleOwner, Observer { dataObj ->
                when (dataObj) {
                    is DataWrapper.Success -> {
                        dataObj.data?.let {
                            mHistoricalAdapter = HistoricalAdapter()
                            mBinding.historicalRv.adapter = mHistoricalAdapter
                            mHistoricalAdapter.setData(it.rates)
                        }
                        mBinding.historicalProgressBar.visibility = View.GONE
                        mBinding.historicalProgressBar.visibility = View.INVISIBLE
                        mBinding.historicalRv.visibility = View.VISIBLE
                        mBinding.historicalBaseTv.visibility = View.VISIBLE
                        mBinding.historicalDateTv.visibility = View.VISIBLE
                    }

                    is DataWrapper.Error -> {
                        mBinding.historicalProgressBar.visibility = View.GONE
                        mBinding.historicalProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.timeout_explanation),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(TAG, "Couldn't perform an api call: ${dataObj.message}")
                    }

                    else -> {}
                }

            })
        }

    /** Track network state. Perform certain actions when available. Otherwise, display monit that'll inform user about necessity of providing internet connection in order to user this fragment */
    private val internetState: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.networkState.collect { state ->
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        when (state) {
                            is DataWrapper.Success -> {
                                if (state.data.toString() == "Available") {
                                    mIsInternetProvided = true
                                    mBinding.historicalNoNetwork.visibility = View.GONE
                                    mBinding.historicalNoNetwork.visibility = View.INVISIBLE
                                    mBinding.historicalInfo.visibility = View.VISIBLE
                                    mBinding.historicalDt.visibility = View.VISIBLE
                                    mBinding.historicalSaveDate.visibility = View.VISIBLE

                                    baseCurr.start()
                                } else {
                                    mIsInternetProvided = false
                                    noInternetViews()
                                }
                            }

                            is DataWrapper.Error -> {
                                Log.e(
                                    TAG,
                                    "couldn't retrieve network state. Exception: ${state.message}"
                                )
                            }
                        }
                    }
                }
            }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricalRatesBinding.inflate(inflater, container, false)
        mBinding.historicalRv.layoutManager = LinearLayoutManager(this.context)
        val view = mBinding.root

        mCalendar.set(1999, 2, 1)
        mBinding.historicalDt.minDate = mCalendar.timeInMillis
        mBinding.historicalDt.maxDate = Calendar.getInstance().timeInMillis

        // After refreshing layout, reset UI to the default state, and observe the base currency once again, so user will see "default" base currency
        mBinding.historicalRefreshContainer.setOnRefreshListener {
            mViewModel.clearResponse()

            /** If user refreshed layout uncheck every position in ListView */
            for (i in 0 until mBinding.historicalSymbolsLv.size) {
                mBinding.historicalSymbolsLv.setItemChecked(i, false)
            }
            /** display appropriate mode of the app, according to the availability of the internet */
            if (mIsInternetProvided) {
                setDefaultVisibility()
            } else {
                noInternetViews()
            }
            mBinding.historicalRefreshContainer.isRefreshing = false
        }

        mBinding.historicalSaveDate.setOnClickListener {
            getDate()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** In case where user will enter this fragment without internet connection,
        / the coroutine responsible for tracking the state of internet connection won't be triggered.
        / Display views like there's no internet connectivity, after user will turn on internet, it'll be changed immediately */
        noInternetViews()
        internetState.start()

        mBinding.historicalChangeBaseIcon.setOnClickListener {
            //In case where user will navigate back from the change base currency fragment, clear the picked currencies list.
            mPickedCurrencies.clear()
            val mFragmentTagViewModel: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            mFragmentTagViewModel.setMoveFlag(true)
        }
    }

    //  Get picked date, and store it in mDate variable in format of 'yyyy-mm-dd'
    @SuppressLint("SimpleDateFormat")
    private fun getDate() {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        cal.set(Calendar.YEAR, mBinding.historicalDt.year)
        cal.set(Calendar.MONTH, mBinding.historicalDt.month)
        cal.set(Calendar.DATE, mBinding.historicalDt.dayOfMonth)
        mDate = sdf.format(cal.time).toString()
        setVisibilityToLv()
    }

    /** This function is kind of a bypass, since we can't just clear the list, and initiate it with 'mCurrencyList' because there will be no effect of it
    The list has some "deeper" reference. There will be two, separated list. One, for spinner, with "Currency" header inside, and second one without this header  */
    private fun deleteBaseFromTheList() {
        val listForSpinner: MutableList<String> = mutableListOf()
        val listForLV: MutableList<String> = mutableListOf()
        mCurrencyList.forEach {
            listForSpinner.add(it)
            listForLV.add(it)
        }

        listForSpinner.removeIf { it == mBaseCurrency }
        listForLV.removeIf { it == mBaseCurrency }
        //Remove "Select currency" value from the list, that is intended for ListView
        listForLV.removeAt(0)

        setupSpinner(listForSpinner)
        setupListView(listForLV)
    }

    // Prepare spinner to display available currencies to change base currency, but only temporary. It will NOT affect the database.
    private fun setupSpinner(currencies: MutableList<String>) {
        var mIsTouched = false
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencies)
        mBinding.historicalChangeBase.adapter = adapter
        mBinding.historicalChangeBase.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    if (currencies[p2] == "Select currency") mIsTouched = false
                    /** By using boolean variable: "mIsTouched" we can avoid self picking first value from the spinner   */
                    if (mIsTouched) {
                        mBaseCurrency = currencies[p2]
                        mBinding.historicalBaseTv.text =
                            String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                        deleteBaseFromTheList()
                    } else {
                        mIsTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.e(TAG, "onNothingSelected in historical spinner ")
                }
            }
    }

    //Prepare ListView to display available currencies to pick up, as a reference to historical rates of selected base currency
    private fun setupListView(list: MutableList<String>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)
        mBinding.historicalSymbolsLv.adapter = adapter
        mBinding.historicalSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mBinding.historicalSymbolsLv.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {}

            /** Since this ListView, is not a direct child of SwipeRefreshLayout, whenever user will drag down the ListView, it'll be immposible to scroll up.
             * As a workaround I'm setting the Scroll listener, which will enable SwipeRefreshLayout whenever the ListView will be scrolled to the top. */
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
                mBinding.historicalRefreshContainer.isEnabled = p1 == 0
            }
        })
        mBinding.historicalSymbolsLv.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    /** After every click, check if total selected amount of symbols is <= 30.
                     * If user will try to select more than 30 symbols, inform him that he can't select more than 30
                     * The limitation is result from not getting data from the server. Probably because of the retrofit wait time limit
                     * **/

                    if (mBinding.historicalSymbolsLv.checkedItemCount > 30) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.cant_select_more_currencies),
                            Toast.LENGTH_SHORT
                        ).show()
                        mBinding.historicalSymbolsLv.setItemChecked(position, false)
                    }
                }
            }
        mBinding.historicalSaveSymbols.setOnClickListener {

            /** Add to the created list all of the checked symbols. Next function will convert them into String */
            for (i in 0 until list.size) {
                if (mBinding.historicalSymbolsLv.isItemChecked(i)) {
                    mPickedCurrencies.add(list[i])
                }
            }
            prepareViewsForRv()
            mApiCall.start()
        }
    }

    //  Prepare views to display RecyclerView. Delete unneeded views
    private fun prepareViewsForRv() {
        mBinding.historicalSelectInfo.visibility = View.GONE
        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
        mBinding.historicalSymbolsLv.visibility = View.GONE
        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
        mBinding.historicalSaveSymbols.visibility = View.GONE
        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
        mBinding.historicalChangeInfo.visibility = View.GONE
        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
        mBinding.historicalChangeBase.visibility = View.GONE
        mBinding.historicalChangeBase.visibility = View.INVISIBLE
        mBinding.historicalBaseTv.visibility = View.GONE
        mBinding.historicalBaseTv.visibility = View.INVISIBLE
        mBinding.historicalDateTv.visibility = View.GONE
        mBinding.historicalDateTv.visibility = View.INVISIBLE
        mBinding.historicalNoNetwork.visibility = View.GONE
        mBinding.historicalNoNetwork.visibility = View.INVISIBLE
        mBinding.historicalInfo.visibility = View.GONE
        mBinding.historicalInfo.visibility = View.INVISIBLE
        mBinding.historicalDt.visibility = View.GONE
        mBinding.historicalDt.visibility = View.INVISIBLE
        mBinding.historicalSaveDate.visibility = View.GONE
        mBinding.historicalSaveDate.visibility = View.INVISIBLE

        mBinding.historicalProgressBar.visibility = View.VISIBLE
    }

    //  Prepare views to display ListView. Make unneeded views invisible
    private fun setVisibilityToLv() {
        mBinding.historicalInfo.visibility = View.GONE
        mBinding.historicalInfo.visibility = View.INVISIBLE
        mBinding.historicalDt.visibility = View.GONE
        mBinding.historicalDt.visibility = View.INVISIBLE
        mBinding.historicalSaveDate.visibility = View.GONE
        mBinding.historicalSaveDate.visibility = View.INVISIBLE

        mBinding.historicalSelectInfo.visibility = View.VISIBLE
        mBinding.historicalSymbolsLv.visibility = View.VISIBLE
        mBinding.historicalSaveSymbols.visibility = View.VISIBLE
        mBinding.historicalChangeInfo.visibility = View.VISIBLE
        mBinding.historicalChangeBase.visibility = View.VISIBLE
        mBinding.historicalBaseTv.visibility = View.VISIBLE
        mBinding.historicalDateTv.visibility = View.VISIBLE
        mBinding.historicalNoNetwork.visibility = View.GONE
        mBinding.historicalNoNetwork.visibility = View.INVISIBLE

        mBinding.historicalDateTv.text = String.format(getString(R.string.formatted_date), mDate)
        mBinding.historicalBaseTv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)

        Toast.makeText(
            activity,
            getString(R.string.select_up_to_30_currencies),
            Toast.LENGTH_SHORT
        ).show()
        allCurrenciesJob.start()
    }

    // Prepare default visibility - it is mainly needed after user will refresh the layout
    private fun setDefaultVisibility() {
        mBinding.historicalInfo.visibility = View.VISIBLE
        mBinding.historicalDt.visibility = View.VISIBLE
        mBinding.historicalSaveDate.visibility = View.VISIBLE

        mBinding.historicalSelectInfo.visibility = View.GONE
        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
        mBinding.historicalSymbolsLv.visibility = View.GONE
        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
        mBinding.historicalSaveSymbols.visibility = View.GONE
        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
        mBinding.historicalChangeInfo.visibility = View.GONE
        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
        mBinding.historicalChangeBase.visibility = View.GONE
        mBinding.historicalChangeBase.visibility = View.INVISIBLE
        mBinding.historicalBaseTv.visibility = View.GONE
        mBinding.historicalBaseTv.visibility = View.INVISIBLE
        mBinding.historicalDateTv.visibility = View.GONE
        mBinding.historicalDateTv.visibility = View.INVISIBLE
        mBinding.historicalDateTv.visibility = View.GONE
        mBinding.historicalDateTv.visibility = View.INVISIBLE
        mBinding.historicalRv.visibility = View.GONE
        mBinding.historicalRv.visibility = View.INVISIBLE
        mBinding.historicalNoNetwork.visibility = View.GONE
        mBinding.historicalNoNetwork.visibility = View.INVISIBLE

        mPickedCurrencies.clear()
        mCurrencyList.clear()
        internetState.start()
    }

    // display in case there is no internet connection, or as a default (when there's no data about network services)
    private fun noInternetViews() {
        mBinding.historicalNoNetwork.visibility = View.VISIBLE

        mBinding.historicalInfo.visibility = View.GONE
        mBinding.historicalInfo.visibility = View.INVISIBLE
        mBinding.historicalDt.visibility = View.GONE
        mBinding.historicalDt.visibility = View.INVISIBLE
        mBinding.historicalSaveDate.visibility = View.GONE
        mBinding.historicalSaveDate.visibility = View.INVISIBLE
        mBinding.historicalRv.visibility = View.GONE
        mBinding.historicalRv.visibility = View.INVISIBLE
        mBinding.historicalSelectInfo.visibility = View.GONE
        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
        mBinding.historicalSymbolsLv.visibility = View.GONE
        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
        mBinding.historicalSaveSymbols.visibility = View.GONE
        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
        mBinding.historicalChangeInfo.visibility = View.GONE
        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
        mBinding.historicalChangeBase.visibility = View.GONE
        mBinding.historicalChangeBase.visibility = View.INVISIBLE
        mBinding.historicalBaseTv.visibility = View.GONE
        mBinding.historicalBaseTv.visibility = View.INVISIBLE
        mBinding.historicalDateTv.visibility = View.GONE
        mBinding.historicalDateTv.visibility = View.INVISIBLE
    }
}

