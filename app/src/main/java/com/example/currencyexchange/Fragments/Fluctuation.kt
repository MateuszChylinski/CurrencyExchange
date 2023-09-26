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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.FluctuationViewModel
import com.example.currencyexchange.ViewModels.FragmentTagViewModel
import com.example.currencyexchange.databinding.FragmentFluctuationBinding
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class Fluctuation : Fragment() {
    private val TAG = "Fluctuation"

    private val mViewModel: FluctuationViewModel by activityViewModels()
    private var _binding: FragmentFluctuationBinding? = null
    private val mBinding get() = _binding!!

    @SuppressLint("SimpleDateFormat")
    private val mSdf = SimpleDateFormat("yyyy-MM-dd")
    private val mCalendar = Calendar.getInstance()
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private val mYearInMs = 31556926000
    private val mDayInMs = 86400000

    private var startDate: String = "default"
    private var endDate: String = "default"

    private var mBaseCurrency: String = "default"
    private val mCurrencies: MutableList<String> = mutableListOf()
    private val mPickedCurrencies: MutableList<String> = mutableListOf()


    /** Track network state. Perform certain actions when available. Otherwise, display monit that'll inform user about necessity of providing internet connection in order to user this fragment */
    private val networkStateTracker: Job
        get() = viewLifecycleOwner.lifecycleScope.launch {
            setupViewNoInternet()
            mViewModel.networkState.collect { status ->
                when (status) {
                    is DataWrapper.Success -> {
                        if (status.data.toString() == "Available") {
                            mBinding.fluctuationNoInternet.visibility = View.INVISIBLE

                            mBinding.fluctuationFromDt.visibility = View.VISIBLE
                            mBinding.fluctuationFromCenterTv.visibility = View.VISIBLE
                            mBinding.fluctuationSetFromOk.visibility = View.VISIBLE

                            baseCurr.start()
                            currencies.start()
                            apiCall.start()
                        } else {
                            setupViewNoInternet()
                        }
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve network services status. Exception: ${status.message}"
                        )
                    }
                }
            }
        }

    /** Retrieve base currency from the database */
    private val baseCurr: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.baseCurrency.collect { currency ->
                when (currency) {
                    is DataWrapper.Success -> {
                        mBaseCurrency = currency.data?.baseCurrency.toString()
                        mBinding.fluctuationBaseCurrencyTv.text = String.format(
                            getString(R.string.formatted_base_currency),
                            mBaseCurrency
                        )
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't collect base currency from the ViewModel. ${currency.message}"
                        )
                    }
                }
            }
        }

    /** Retrieve list of currencies from the database */
    private val currencies: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.allCurrencies.collect { currencies ->
                when (currencies) {
                    is DataWrapper.Success -> {
                        currencies.data?.currencyData?.forEach {
                            mCurrencies.add(it.key)
                        }
                        if (!mCurrencies.contains("Select currency")) {
                            mCurrencies.add(0, "Select currency")
                        }
                        deleteBaseCurrencyFromList()
                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve all available currencies from the ViewModel. ${currencies.message}",
                        )
                    }
                }
            }
        }

    /** Make an api call, and observe LiveData from ViewModel */
    private val apiCall: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                mViewModel.fetchFluctuation(
                    baseCurrency = mBaseCurrency,
                    selectedCurrencies = mPickedCurrencies.joinToString(separator = ","),
                    startDate = startDate,
                    endDate = endDate
                )

                mViewModel.fluctuationResponse.observe(viewLifecycleOwner, Observer { fluctuation ->
                    when (fluctuation) {
                        is DataWrapper.Success -> {
                            mFluctuationAdapter = FluctuationAdapter()
                            fluctuation.data?.rates?.let { mFluctuationAdapter?.setData(it) }
                            mBinding.fluctuationRv.adapter = mFluctuationAdapter
                            mBinding.fluctuationProgress.visibility = View.INVISIBLE

                        }

                        is DataWrapper.Error -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.timeout_explanation),
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "Couldn't get fluctuation: ${fluctuation.message}")
                        }
                    }
                })
            }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFluctuationBinding.inflate(inflater, container, false)
        val view = mBinding.root

        networkStateTracker.start()


        /** Reload UI to 'default' state (as it was while entering the layout for the first time) */
        mBinding.fluctuationRefreshContainer.setOnRefreshListener {
            defaultViewsSetup()
            mBinding.fluctuationRefreshContainer.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * Setup date picker where user can choose since when he would like to display fluctuation from
         * Since the API allow to see fluctuation back to 365 days, the minimum date to pick is set to be year ago from current date
         */
        mBinding.fluctuationFromDt.minDate = Calendar.getInstance().timeInMillis - mYearInMs
        mBinding.fluctuationFromDt.maxDate = Calendar.getInstance().timeInMillis

        mBinding.fluctuationSetFromOk.setOnClickListener {
            getDateFromUser(1)
            setupViewsToGetDate()
        }
        mBinding.fluctuationSetToOk.setOnClickListener {
            getDateFromUser(2)
            prepareViewsForListView()
        }
        /** Prepare a flag, which can trigger the Navigation Components in the PagerBase fragment (which is ViewHolder host)
         * to move user to the ChangeBaseCurrency fragment, where user can set new base currency (permanently, in database) */
        mBinding.fluctuationChangeBaseCurrency.setOnClickListener {
            val mFragmentTagViewModel: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            mFragmentTagViewModel.setMoveFlag(true)
        }
    }

    /** Provide data from DatePicker to the ViewModel.
     * Later on, these values can be useful for displaying picked date, and performing api call*/
    @SuppressLint("SimpleDateFormat")
    private fun getDateFromUser(selection: Int) {

        /** Selection is an integer, which allow to select appropriate DatePicker
        Since there will be two DatePickers, the first one (start date) will be marked as '1'.
        The second (end date) will be marked as '2'   */
        when (selection) {
            1 -> {
                mCalendar.set(Calendar.YEAR, mBinding.fluctuationFromDt.year)
                mCalendar.set(Calendar.MONTH, mBinding.fluctuationFromDt.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.fluctuationFromDt.dayOfMonth)
                startDate = mSdf.format(mCalendar.time).toString()


                /** After choosing starting date, set the "ending" date, to be a year later after the "from" date. */
                mBinding.fluctuationToDt.minDate = (mCalendar.timeInMillis + mDayInMs)
                mBinding.fluctuationToDt.maxDate = Calendar.getInstance().timeInMillis
            }

            2 -> {
                mCalendar.set(Calendar.YEAR, mBinding.fluctuationToDt.year)
                mCalendar.set(Calendar.MONTH, mBinding.fluctuationToDt.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.fluctuationToDt.dayOfMonth)
                endDate = mSdf.format(mCalendar.time).toString()
            }
        }
    }

    /** This function is kind of a bypass, since we can't just clear the list, and initiate it with 'mCurrencyList' because there will be no effect of it
    The list has some "deeper" reference. There will be two, separated list. One, for spinner, with "Currency" header inside, and second one without this header  */
    private fun deleteBaseCurrencyFromList() {
        val spinnerList: MutableList<String> = mutableListOf()
        val lvList: MutableList<String> = mutableListOf()

        mCurrencies.forEach {
            spinnerList.add(it)
            lvList.add(it)
        }

        spinnerList.removeIf { it == mBaseCurrency }
        lvList.removeIf { it == mBaseCurrency }

        //Delete "Select currency" value from the list intended for ListView.
        lvList.removeAt(0)

        setupBaseCurrencySpinner(spinnerList)
        setupListView(lvList)
    }

    /** Setup spinner that allow user to change the base currency. It will NOT affect the database, it will be a temporary change. */
    private fun setupBaseCurrencySpinner(currencyNames: MutableList<String>) {
        var mIsTouched = false
        val mSpinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mBinding.fluctuationSelectBaseCurrency.adapter = mSpinnerAdapter
        mBinding.fluctuationSelectBaseCurrency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (currencyNames[p2] == "Select currency") mIsTouched = false
                    if (mIsTouched) {
                        mIsTouched = false
                        mBaseCurrency = currencyNames[p2]
                        mBinding.fluctuationBaseCurrencyTv.text =
                            String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                        deleteBaseCurrencyFromList()
                    } else {
                        mIsTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.e(TAG, "onNothingSelected: IN FLUCTUATION SPINNER")
                }
            }
    }

    /** Setup ListView, so user can pick currencies, that he would like to compare with base currency   */
    private fun setupListView(list: MutableList<String>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)

        mBinding.fluctuationSelectSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mBinding.fluctuationSelectSymbolsLv.adapter = adapter
        mBinding.fluctuationSelectSymbolsLv.setOnScrollListener(object :
            AbsListView.OnScrollListener {
            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {}

            /** Since this ListView, is not a direct child of SwipeRefreshLayout, whenever user will drag down the ListView, it'll be immposible to scroll up.
             * As a workaround I'm setting the Scroll listener, which will enable SwipeRefreshLayout whenever the ListView will be scrolled to the top. */
            override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
                mBinding.fluctuationRefreshContainer.isEnabled = p1 == 0
            }
        })
        mBinding.fluctuationSelectSymbolsLv.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    /**             After every click, check if total selected amount of symbols is <= 30.
                    //              If user will try to select more than 30 symbols, inform him that he can't select more than 30 **/
                    if (mBinding.fluctuationSelectSymbolsLv.checkedItemCount > 30) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.cant_select_more_currencies),
                            Toast.LENGTH_SHORT
                        ).show()
                        mBinding.fluctuationSelectSymbolsLv.setItemChecked(position, false)
                    }
                }
            }

        mBinding.fluctuationSaveSymbols.setOnClickListener {
            for (i in 0 until list.size) {
                if (mBinding.fluctuationSelectSymbolsLv.isItemChecked(i)) {
                    mPickedCurrencies.add(list[i])
                }
            }
            prepareRecyclerView()
            apiCall.start()

        }
    }



    /** When user will refresh layout, by using the SwiperRefreshLayout, this function will be called.
     *  It'll reset the UI to the "default" state. As it was when user entered the fragment for the first time  */
    private fun defaultViewsSetup() {
        mBinding.fluctuationBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.fluctuationRv.visibility = View.INVISIBLE
        mBinding.fluctuationBaseInRv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectBaseCurrency.visibility = View.INVISIBLE
        mBinding.fluctuationToDt.visibility = View.INVISIBLE
        mBinding.fluctuationFromDate.visibility = View.INVISIBLE
        mBinding.fluctuationToDate.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.INVISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.INVISIBLE

        /** Uncheck previously checked by user currencies    .*/
        for (i in 0 until mBinding.fluctuationSelectSymbolsLv.size) {
            if (mBinding.fluctuationSelectSymbolsLv.isItemChecked(i)) {
                mBinding.fluctuationSelectSymbolsLv.setItemChecked(i, false)
            }
        }

        /** Clear from/to date after reset  */
        mBaseCurrency = String()
        startDate = String()
        endDate = String()
        mCurrencies.clear()
        mPickedCurrencies.clear()

        networkStateTracker.start()
    }


    private fun setupViewNoInternet() {
        mBinding.fluctuationNoInternet.visibility = View.VISIBLE

        mBinding.fluctuationFromCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationFromDt.visibility = View.INVISIBLE
        mBinding.fluctuationSetFromOk.visibility = View.INVISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.fluctuationRv.visibility = View.INVISIBLE
        mBinding.fluctuationBaseInRv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectBaseCurrency.visibility = View.INVISIBLE
        mBinding.fluctuationToDt.visibility = View.INVISIBLE
        mBinding.fluctuationFromDate.visibility = View.INVISIBLE
        mBinding.fluctuationToDate.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.INVISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.INVISIBLE
        mBinding.fluctuationToCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationSetToOk.visibility = View.INVISIBLE
    }

    private fun setupViewsToGetDate() {
        mBinding.fluctuationFromCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationFromDt.visibility = View.INVISIBLE
        mBinding.fluctuationSetFromOk.visibility = View.INVISIBLE

        mBinding.fluctuationToCenterTv.visibility = View.VISIBLE
        mBinding.fluctuationToDt.visibility = View.VISIBLE
        mBinding.fluctuationSetToOk.visibility = View.VISIBLE
    }

    private fun prepareViewsForListView() {
        mBinding.fluctuationToCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationToDt.visibility = View.INVISIBLE
        mBinding.fluctuationSetToOk.visibility = View.INVISIBLE

        mBinding.fluctuationSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.VISIBLE

        mBinding.fluctuationFromDate.visibility = View.VISIBLE
        mBinding.fluctuationToDate.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.VISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.VISIBLE

        mBinding.fluctuationFromDate.text =
            String.format(getString(R.string.formatted_from), startDate)
        mBinding.fluctuationToDate.text =
            String.format(getString(R.string.formatted_to), endDate)

        Toast.makeText(
            activity,
            getString(R.string.select_up_to_30_currencies),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun prepareRecyclerView() {
        mBinding.fluctuationBaseCurrencyTv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)
        mBinding.fluctuationBaseInRv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)
        mBinding.fluctuationRv.layoutManager = LinearLayoutManager(this.requireContext())

        mBinding.fluctuationProgress.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.fluctuationRv.visibility = View.VISIBLE
        mBinding.fluctuationBaseInRv.visibility = View.VISIBLE

        mBinding.fluctuationSelectBaseCurrency.visibility = View.INVISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.fluctuationFromDate.visibility = View.INVISIBLE
        mBinding.fluctuationToDate.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.INVISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.INVISIBLE
    }
}
