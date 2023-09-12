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
    private var bool: Boolean = true
    private var internetStatus = false
    private val mStartingDateInMs = 946684800000
    private val mDayInMs = 86400000
    private var mSelectedCurrencies: MutableList<String> = mutableListOf()
    private var mCurrenciesList: MutableList<String> = mutableListOf()

    private val baseCurrencyJob: Job
        get() = viewLifecycleOwner.lifecycleScope.launch {
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

    private val allCurrenciesJob: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch() {
                mViewModel.allCurrencies.collect { currencies ->
                    when (currencies) {
                        is DataWrapper.Success -> {
                            currencies.data?.currencyData?.forEach {
                                mCurrenciesList.add(it.key)
                            }
                            mViewModel.deleteBaseTest(mCurrenciesList, "EUR", true)
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
                                    allCurrenciesJob.cancel()
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

    private val apiCall: Job
        get() =
            viewLifecycleOwner.lifecycleScope.launch {
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
                            }
                            testingChart(
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
            //TODO             defaultViewsSetup()
            mBinding.timeSeriesRefreshContainer.isRefreshing = false
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.timeSeriesStartDp.minDate = mStartingDateInMs
        mBinding.timeSeriesStartDp.maxDate = mCalendar.timeInMillis.minus(mDayInMs)
        mBinding.timeSeriesStartBtn.setOnClickListener {
            setViewsToGetEndDate()
            getDateFromUser(1)
        }
        mBinding.timeSeriesEndBtn.setOnClickListener {
            getDateFromUser(2)
            setViewsForListView()
        }
        mBinding.timeSeriesChangeBaseCurrency.setOnClickListener {
            val changeCurrFragment: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() }
            )
            changeCurrFragment.setMoveFlag(true)
        }
    }

    private fun getDateFromUser(selection: Int) {
        when (selection) {
            1 -> {
                mCalendar.set(Calendar.YEAR, mBinding.timeSeriesStartDp.year)
                mCalendar.set(Calendar.MONTH, mBinding.timeSeriesStartDp.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.timeSeriesStartDp.dayOfMonth)
                mStartDate = mSdf.format(mCalendar.time).toString()

                mBinding.timeSeriesEndDp.minDate = (mCalendar.timeInMillis.plus(mDayInMs))
                mBinding.timeSeriesEndDp.maxDate = Calendar.getInstance().timeInMillis
            }
            2 -> {
                mCalendar.set(Calendar.YEAR, mBinding.timeSeriesEndDp.year)
                mCalendar.set(Calendar.MONTH, mBinding.timeSeriesEndDp.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.timeSeriesEndDp.dayOfMonth)
                mEndDate = mSdf.format(mCalendar.time).toString()
            }
        }
    }

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

    private fun setViewsToGetEndDate() {
        mBinding.timeSeriesStartDp.visibility = View.INVISIBLE
        mBinding.timeSeriesStartBtn.visibility = View.INVISIBLE

        mBinding.timeSeriesEndDp.visibility = View.VISIBLE
        mBinding.timeSeriesEndBtn.visibility = View.VISIBLE
    }

    private fun setViewsForListView() {
        baseCurrencyJob.start()
        allCurrenciesJob.start()

        mBinding.timeSeriesInfo.visibility = View.INVISIBLE
        mBinding.timeSeriesEndDp.visibility = View.INVISIBLE
        mBinding.timeSeriesEndBtn.visibility = View.INVISIBLE

        mBinding.timeSeriesSelectSymbolsLv.visibility = View.VISIBLE
        mBinding.timeSeriesSaveSymbols.visibility = View.VISIBLE
        mBinding.timeSeriesBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.timeSeriesStartDate.visibility = View.VISIBLE
        mBinding.timeSeriesEndDate.visibility = View.VISIBLE

        mBinding.timeSeriesStartDate.text = getString(R.string.formatted_from, mStartDate)
        mBinding.timeSeriesEndDate.text = getString(R.string.formatted_to, mEndDate)
    }


    private fun setViewsToDisplayChart() {
        mBinding.timeSeriesSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.timeSeriesSaveSymbols.visibility = View.INVISIBLE
        mBinding.timeSeriesBaseCurrencyTv.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.timeSeriesStartDate.visibility = View.INVISIBLE
        mBinding.timeSeriesEndDate.visibility = View.INVISIBLE
        mBinding.timeSeriesSelectBaseCurrency.visibility = View.INVISIBLE

        mBinding.timeSeriesChart.visibility = View.VISIBLE
    }

    /** This function is kind of a bypass, since we can't just clear the list, and initiate it with 'mCurrencyList' because there will be no effect of it
    The list has some "deeper" reference. There will be two, separated list. One, for spinner, with "Currency" header inside, and second one without this header  */
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

    private fun setupListView(currencyList: MutableList<String>) {
        Toast.makeText(requireContext(), getString(R.string.pick_7_currencies), Toast.LENGTH_SHORT)
            .show()

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
            for (i in 0 until mCurrenciesList.size) {
                if (mBinding.timeSeriesSelectSymbolsLv.isItemChecked(i)) {
                    mSelectedCurrencies.add(mCurrenciesList[i])
                }
            }
            apiCall.start()
            setViewsToDisplayChart()
        }
    }

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


    private fun testingChart(
        entries: MutableList<BarEntry>,
        xAxisValues: ArrayList<String>,
        selectedCurrencies: List<String>
    ) {

        val legendEntries = arrayListOf<LegendEntry>()
        val colorsList = mutableListOf<Int>()
        //TODO - adjust colors after changing UI. These colors are temporary.
        Log.i(TAG, "testingChart: $selectedCurrencies")

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
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        bool = !bool
        set!!.setDrawValues(bool)
    }

    override fun onNothingSelected() {
        bool = !bool
        set!!.setDrawValues(bool)
    }
}