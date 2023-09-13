package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.FragmentTagViewModel
import com.example.currencyexchange.ViewModels.TimeSeriesViewModel
import com.example.currencyexchange.databinding.FragmentTimeSeriesBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random

class TimeSeries : Fragment(), OnChartValueSelectedListener {
    @SuppressLint("SimpleDateFormat")
    private val mSdf = SimpleDateFormat("yyyy-MM-dd")
    private val mCalendar = Calendar.getInstance()

    private var mTimeSeriesBinding: FragmentTimeSeriesBinding? = null
    private val mBinding get() = mTimeSeriesBinding!!
    private val mViewModel: TimeSeriesViewModel by activityViewModels()
    private var set: BarDataSet? = null

    private var mBaseCurrency: String = "default"
    private var mStartDate: String = "default"
    private var mEndDate: String = "default"
    private var isClicked: Boolean = true
    private var internetStatus = false
    private val mStartingDateInMs = 946684800000
    private val mDayInMs = 86400000
    private var mSelectedCurrencies: MutableList<String> = mutableListOf()
    private var mCurrenciesList: MutableList<String> = mutableListOf()


    /** Collect base currency from the database **/
    private val baseCurrencyJob: Job
        get() = viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
            mViewModel.baseCurrency.collect { currency ->
                when (currency) {
                    is DataWrapper.Success -> {
                        mBaseCurrency = currency.data?.baseCurrency.toString()
                        mBinding.timeSeriesBaseCurrencyTv.text =
                            getString(R.string.formatted_base_currency, mBaseCurrency)
                    }

                    is DataWrapper.Error -> {
                        Log.i(
                            TAG,
                            "Couldn't retrieve base currency from the database. ${currency.message}: "
                        )
                    }
                }
            }
        }

    /** Collect all available currencies from the database **/
    private val allCurrenciesJob: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                mViewModel.allCurrencies.collect { currencies ->
                    when (currencies) {
                        is DataWrapper.Success -> {
                            currencies.data?.currencyData?.forEach {
                                mCurrenciesList.add(it.key)
                            }
                            // Reason for doing that, is because in spinner, there'll be always something picked, so this is kind a work around way, to let user know, that he wasn't picked currently displayed currency in spinner
                            if (!mCurrenciesList.contains("Select currency")) {
                                mCurrenciesList.add(0, "Select currency")
                            }
                            deleteBaseCurrencyFromList()
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "Couldn't retrieve currencies from the database. ${currencies.message}"
                            )
                        }
                    }
                }
            }


    /** Observe network state, in order to display for user warning, that he need to provide internet connection, or if the mobile device is already connected, let the user use this fragment. **/

    private val networkObserver: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.networkStatus.collect { networkStatus ->
                        when (networkStatus) {
                            is DataWrapper.Success -> {
                                internetStatus = networkStatus.data.toString() == "Available"
                                if (networkStatus.data.toString() == "Available") {
                                    internetStatus = true
                                } else {
                                    internetStatus = false
                                    mStartDate = ""
                                    mEndDate = ""
//                                    allCurrenciesJob.cancel()
                                }
                                setViewsToGetStartDate(internetStatus)
                            }

                            is DataWrapper.Error -> {
                                Log.i(
                                    TAG,
                                    "Couldn't retrieve network status ${networkStatus.message}: "
                                )
                            }
                        }
                    }
                }
            }

    /** Change picked currencies into string chain, make an api call with provided data, and observe the result **/
    private val apiCall: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                var currencyChain = ""
                for (i in 0 until mSelectedCurrencies.size) {
                    currencyChain += "${mSelectedCurrencies[i]}, "
                }

                mViewModel.fetchTimeSeriesData(
                    mBaseCurrency,
                    currencyChain,
                    mStartDate,
                    mEndDate
                )
                mViewModel.timeSeriesData.observe(viewLifecycleOwner, Observer { status ->
                    when (status) {
                        is DataWrapper.Success -> {
                            val testApiData: MutableList<BarEntry> = ArrayList()
                            formatDate(status.data?.timeSeriesRates?.keys?.toList()!!)
                            status.data.timeSeriesRates.entries.forEachIndexed { index, entry ->
                                testApiData.add(
                                    BarEntry(
                                        index.toFloat(),
                                        entry.value.values.map { it.toFloat() }.toFloatArray()
                                    )
                                )

                                deleteBaseCurrencyFromList()
                            }
                            prepareChart(
                                testApiData,
                                formatDate(status.data.timeSeriesRates.keys.toList()),
                                mSelectedCurrencies
                            )
                        }

                        is DataWrapper.Error -> {
                            Log.i(TAG, "onCreateView: TEST API CALL FAILED ${status.message}")
                        }
                    }
                })
            }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mTimeSeriesBinding = FragmentTimeSeriesBinding.inflate(layoutInflater)
        val view = mBinding.root

        networkObserver.start()
        mBinding.timeSeriesRefreshContainer.setOnRefreshListener {
            defaultViews()
            mBinding.timeSeriesRefreshContainer.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.timeSeriesStartDp.minDate = mStartingDateInMs
        mBinding.timeSeriesStartDp.maxDate = mCalendar.timeInMillis.minus(mDayInMs)

        mBinding.timeSeriesStartBtn.setOnClickListener {
            getDateFromUser(1)
            setViewsToGetEndDate()
        }
        mBinding.timeSeriesEndBtn.setOnClickListener {
            getDateFromUser(2)
            setViewsForListView()

            Toast.makeText(
                requireContext(),
                getString(R.string.pick_7_currencies),
                Toast.LENGTH_SHORT
            )
                .show()
        }
        mBinding.timeSeriesChangeBaseCurrency.setOnClickListener {
            val changeCurrFragment: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() }
            )
            changeCurrFragment.setMoveFlag(true)
        }
    }

    /** After triggering the SwipeRefreshLayout, manipulate views, to the state when user enter the fragment for the first time **/
    private fun defaultViews() {
        mBinding.timeSeriesEndDp.visibility = View.INVISIBLE
        mBinding.timeSeriesEndBtn.visibility = View.INVISIBLE
        mBinding.timeSeriesEndDate.visibility = View.INVISIBLE
        mBinding.timeSeriesBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.INVISIBLE
        mBinding.timeSeriesStartDate.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectSymbolsTv.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.timeSeriesSaveSymbols.visibility = View.INVISIBLE
        mBinding.timeSeriesChart.visibility = View.INVISIBLE

        mBinding.timeSeriesInfo.visibility = View.VISIBLE
        mBinding.timeSeriesStartDp.visibility = View.VISIBLE
        mBinding.timeSeriesStartBtn.visibility = View.VISIBLE

        mSelectedCurrencies.clear()
        mBinding.timeSeriesChart.invalidate()
        mBinding.timeSeriesChart.refreshDrawableState()
    }


    /** Get from/to date from user **/
    private fun getDateFromUser(selection: Int) {
        when (selection) {
            1 -> {
                mCalendar.set(Calendar.YEAR, mBinding.timeSeriesStartDp.year)
                mCalendar.set(Calendar.MONTH, mBinding.timeSeriesStartDp.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.timeSeriesStartDp.dayOfMonth)
                mStartDate = mSdf.format(mCalendar.time).toString()

                val prepareMaxDate = mCalendar.clone() as Calendar
                prepareMaxDate.add(Calendar.YEAR, 1)

                if (prepareMaxDate.after(Calendar.getInstance())) {
                    prepareMaxDate.time = Calendar.getInstance().time
                }
                mBinding.timeSeriesEndDp.minDate = mCalendar.timeInMillis
                mBinding.timeSeriesEndDp.maxDate = prepareMaxDate.timeInMillis
            }

            2 -> {
                mCalendar.set(Calendar.YEAR, mBinding.timeSeriesEndDp.year)
                mCalendar.set(Calendar.MONTH, mBinding.timeSeriesEndDp.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.timeSeriesEndDp.dayOfMonth)
                mEndDate = mSdf.format(mCalendar.time).toString()
            }
        }
    }

    /** Depending on the current network state (is it provided, or not) let the user work with this fragment (if there is internet provided) or display notification, that he need to provide internet to work with this fragment **/
    private fun setViewsToGetStartDate(status: Boolean) {
        if (status) {
            mBinding.timeSeriesNoInternet.visibility = View.INVISIBLE
            mBinding.timeSeriesInfo.visibility = View.VISIBLE
            mBinding.timeSeriesStartDp.visibility = View.VISIBLE
            mBinding.timeSeriesStartBtn.visibility = View.VISIBLE
        } else {
            mBinding.timeSeriesNoInternet.visibility = View.VISIBLE
            mBinding.timeSeriesInfo.visibility = View.INVISIBLE
            mBinding.timeSeriesStartDp.visibility = View.INVISIBLE
            mBinding.timeSeriesStartBtn.visibility = View.INVISIBLE
        }
    }

    /** Manipulate views to let user pick end date. Hide "start date" views **/
    private fun setViewsToGetEndDate() {
        mBinding.timeSeriesStartDp.visibility = View.INVISIBLE
        mBinding.timeSeriesStartBtn.visibility = View.INVISIBLE

        mBinding.timeSeriesEndDp.visibility = View.VISIBLE
        mBinding.timeSeriesEndBtn.visibility = View.VISIBLE
    }

    /** Manipulate views to let user pick currencies from the list view. Start base currency, and all currencies Jobs. **/
    private fun setViewsForListView() {
        baseCurrencyJob.start()
        allCurrenciesJob.start()

        mBinding.timeSeriesStartDate.text = getString(R.string.formatted_from, mStartDate)
        mBinding.timeSeriesEndDate.text = getString(R.string.formatted_to, mEndDate)

        mBinding.timeSeriesProgressBar.visibility = View.VISIBLE
        mBinding.timeSeriesInfo.visibility = View.INVISIBLE
        mBinding.timeSeriesEndDp.visibility = View.INVISIBLE
        mBinding.timeSeriesEndBtn.visibility = View.INVISIBLE

        mBinding.timeSeriesSelectSymbolsLv.visibility = View.VISIBLE
        mBinding.timeSeriesSaveSymbols.visibility = View.VISIBLE
        mBinding.timeSeriesBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.timeSeriesStartDate.visibility = View.VISIBLE
        mBinding.timeSeriesEndDate.visibility = View.VISIBLE

        mBinding.timeSeriesProgressBar.visibility = View.INVISIBLE
    }

    /** Setup views to display chart with data, that came from api call. **/
    private fun setViewsToDisplayChart() {
        mBinding.timeSeriesSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.timeSeriesSaveSymbols.visibility = View.INVISIBLE
        mBinding.timeSeriesBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.timeSeriesStartDate.visibility = View.INVISIBLE
        mBinding.timeSeriesEndDate.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.INVISIBLE

    }

    /** Provide two lists. One for spinner, second ListView. The difference is in spinner list, where at first index, there will be record 'Select currency'.
     * Remove base currency from both lists, and remove first record, which will be 'Select currency' from ListView  */
    private fun deleteBaseCurrencyFromList() {
        val spinnerList: MutableList<String> = mutableListOf()
        val lvList: MutableList<String> = mutableListOf()

        mCurrenciesList.forEach {
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

    /** Setup spinner, that will allow user to change base currency temporarily **/
    private fun setupBaseCurrencySpinner(currencies: MutableList<String>) {
        var isTouched = false
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencies)
        mBinding.timeSeriesSelectBaseCurrency.adapter = adapter
        mBinding.timeSeriesSelectBaseCurrency.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (isTouched) {
                        isTouched = false
                        mBaseCurrency = currencies[p2]
                        mBinding.timeSeriesBaseCurrencyTv.text =
                            getString(R.string.formatted_base_currency, mBaseCurrency)
                        deleteBaseCurrencyFromList()
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: IN TIME SERIES FRAGMENT")
                }
            }
    }

    /** Setup ListView, and fill it with all available currencies. Inform user that he can select up to 7 currencies **/
    private fun setupListView(currencyList: MutableList<String>) {

        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_list_item_multiple_choice,
            currencyList
        )
        mBinding.timeSeriesSelectSymbolsLv.adapter = adapter
        mBinding.timeSeriesSelectSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mBinding.timeSeriesSelectSymbolsLv.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (mBinding.timeSeriesSelectSymbolsLv.checkedItemCount > 7) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.cant_select_more_currencies),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        mBinding.timeSeriesSelectSymbolsLv.setItemChecked(p2, false)
                    }
                }
            }

        mBinding.timeSeriesSaveSymbols.setOnClickListener {
            for (i in 0 until currencyList.size) {
                if (mBinding.timeSeriesSelectSymbolsLv.isItemChecked(i)) {
                    mSelectedCurrencies.add(currencyList[i])
                }
            }

            if (mSelectedCurrencies.size > 0) {
                mBinding.timeSeriesProgressBar.visibility = View.VISIBLE
                apiCall.start()
                setViewsToDisplayChart()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.select_at_least_one_currency),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /** Format date that will came from api call. **/
    private fun formatDate(dateToFormat: List<String>): ArrayList<String> {
        val toReturn: ArrayList<String> = arrayListOf()
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM", Locale.US)

        for (i in dateToFormat.indices) {
            val date: Date = inputFormat.parse(dateToFormat[i])!!
            toReturn.add(outputFormat.format(date))
        }
        return toReturn
    }

    //TODO - adjust colors after changing UI. These colors are temporary.
    /** Prepare bar chart to display data from api call. **/
    private fun prepareChart(
        entries: MutableList<BarEntry>,
        xAxisValues: ArrayList<String>,
        selectedCurrencies: List<String>
    ) {
        val legendEntries = arrayListOf<LegendEntry>()
        val colorsList = mutableListOf<Int>()
        for (i in selectedCurrencies.indices) {
            val color =
                Color.argb(
                    255,
                    Random().nextInt(256),
                    Random().nextInt(256),
                    Random().nextInt(256)
                )
            if (!colorsList.contains(color)) {
                colorsList.add(color)
            }
        }
        set = BarDataSet(entries, "x")
        set?.colors = colorsList
        set?.highLightAlpha = 0

        val data = BarData(set)
        mBinding.timeSeriesChart.data = data
        mBinding.timeSeriesChart.description?.isEnabled = false
        mBinding.timeSeriesChart.setVisibleXRangeMaximum(6f)
        mBinding.timeSeriesChart.barData?.setValueTextSize(12f)
        mBinding.timeSeriesChart.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        mBinding.timeSeriesChart.setExtraOffsets(0f, 0f, 0f, 15f)

        //xAxis
        mBinding.timeSeriesChart.xAxis?.setDrawLabels(true)
        mBinding.timeSeriesChart.xAxis?.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        mBinding.timeSeriesChart.xAxis?.granularity = 1f
        mBinding.timeSeriesChart.xAxis?.textSize = 11.5f
        mBinding.timeSeriesChart.xAxis?.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        mBinding.timeSeriesChart.xAxis?.labelRotationAngle = -20f
        mBinding.timeSeriesChart.axisRight?.setDrawGridLines(false)
        mBinding.timeSeriesChart.axisLeft.isEnabled = false
        mBinding.timeSeriesChart.axisRight.isEnabled = false

        //legend
        val legend = mBinding.timeSeriesChart.legend
        legend?.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend?.orientation = Legend.LegendOrientation.HORIZONTAL
        legend?.setDrawInside(false)

        for (i in selectedCurrencies.indices) {
            legendEntries.add(
                LegendEntry(
                    selectedCurrencies[i],
                    Legend.LegendForm.SQUARE,
                    15f,
                    15f,
                    null,
                    colorsList[i]
                )
            )
        }

        legend?.setCustom(legendEntries)
        set?.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%2.02f", value)
            }
        }
        mBinding.timeSeriesChart.setOnChartValueSelectedListener(this)

        mBinding.timeSeriesChart.invalidate()
        mBinding.timeSeriesChart.refreshDrawableState()
        mBinding.timeSeriesChart.visibility = View.VISIBLE
        mBinding.timeSeriesProgressBar.visibility = View.INVISIBLE
    }

    /** Let user show, and hide rates of specific currencies **/
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        isClicked = !isClicked
        set!!.setDrawValues(isClicked)
    }

    /** Let user show, and hide rates of specific currencies **/
    override fun onNothingSelected() {
        isClicked = !isClicked
        set!!.setDrawValues(isClicked)
    }
}