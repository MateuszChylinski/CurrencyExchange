package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class HistoricalRates : Fragment() {

    private val TAG = "HistoricalRates"
    private var mDate: String = "default"
    private var mBaseCurrency = "default"
    private var mIsRefreshed: Boolean = false

    private var _binding: FragmentHistoricalRatesBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: HistoricalViewModel by activityViewModels()
    private val mCalendar = Calendar.getInstance()

    private var mHistoricalAdapter: HistoricalAdapter? = null
    private var mCurrencyList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricalRatesBinding.inflate(inflater, container, false)
        mBinding.historicalRv.layoutManager = LinearLayoutManager(this.context)
        val view = mBinding.root

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mViewModel.allCurrencies.collect { currencies ->
                when (currencies) {
                    is DataWrapper.Success -> {
                        currencies.data?.currencyData?.keys?.forEach {
                            mCurrencyList.add(it)
                        }
                        /** Add to the currency list a value called 'Currency' as a row, since spinners by default are picking values when initiated.
                        By adding this "header", user will be able to pick first currency */

                        if (!mCurrencyList.contains("Currency")) {
                            mCurrencyList.add(0, "Currency")
                        }
                        deleteBaseFromTheList()

                    }

                    is DataWrapper.Error -> {
                        Log.e(
                            TAG,
                            "onCreateView: couldn't retrieve all currencies from the viewmodel. ${currencies.message}",
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.historicalData.observe(viewLifecycleOwner, Observer {
                    mHistoricalAdapter = HistoricalAdapter()
                    mHistoricalAdapter?.setData(it.data?.rates!!)
                    mBinding.historicalRv.adapter = mHistoricalAdapter
                })
            }
        }
        // After refreshing layout, reset UI to the default state, and observe the base currency once again, so user will see "default" base currency
        mBinding.historicalRefreshContainer.setOnRefreshListener {
            mBinding.historicalRefreshContainer.isRefreshing = false

            lifecycleScope.launch {
                mViewModel.baseCurrency.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.historicalBaseTv.text = mBaseCurrency
                        }

                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView: couldn't retrieve base currency from the viewmodel after refreshing the layout.\n${currency.message}",
                            )
                        }
                    }
                }
            }
            mIsRefreshed = true
            setDefaultVisibility()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCalendar.set(1999, 2, 1)
        mBinding.historicalDt.minDate = mCalendar.timeInMillis
        mBinding.historicalDt.maxDate = Calendar.getInstance().timeInMillis

        mBinding.historicalChangeBaseIcon.setOnClickListener {
            val mFragmentTagViewModel: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            mFragmentTagViewModel.setMoveFlag(true)
        }
        setDefaultVisibility()
    }

    // Prepare default visibility - it is mainly needed after user will refresh the layout
    private fun setDefaultVisibility() {
        /** If user refreshed layout uncheck every position in ListView */
        if (mIsRefreshed) {
            for (i in 0 until mBinding.historicalSymbolsLv.checkedItemCount) {
                mBinding.historicalSymbolsLv.setItemChecked(i, false)
            }
        }
        mBinding.historicalInfo.visibility = View.VISIBLE
        mBinding.historicalDt.visibility = View.VISIBLE
        mBinding.historicalSaveDate.visibility = View.VISIBLE

        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
        mBinding.historicalChangeBase.visibility = View.INVISIBLE
        mBinding.historicalBaseTv.visibility = View.INVISIBLE
        mBinding.historicalDateTv.visibility = View.INVISIBLE

        mBinding.historicalDateTv.visibility = View.INVISIBLE
        mBinding.historicalRv.visibility = View.INVISIBLE

        mBinding.historicalSaveDate.setOnClickListener {
            getDate()
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

    //  Prepare views to display ListView. Make unneeded views invisible
    private fun setVisibilityToLv() {
        mBinding.historicalInfo.visibility = View.INVISIBLE
        mBinding.historicalDt.visibility = View.INVISIBLE
        mBinding.historicalSaveDate.visibility = View.INVISIBLE

        mBinding.historicalSelectInfo.visibility = View.VISIBLE
        mBinding.historicalSymbolsLv.visibility = View.VISIBLE
        mBinding.historicalSaveSymbols.visibility = View.VISIBLE
        mBinding.historicalChangeInfo.visibility = View.VISIBLE
        mBinding.historicalChangeBase.visibility = View.VISIBLE
        mBinding.historicalBaseTv.visibility = View.VISIBLE
        mBinding.historicalDateTv.visibility = View.VISIBLE

        mBinding.historicalDateTv.text = String.format(getString(R.string.formatted_date), mDate)
        mBinding.historicalBaseTv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)

        Toast.makeText(
            activity,
            getString(R.string.select_up_to_30_currencies),
            Toast.LENGTH_SHORT
        ).show()

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

        if (listForSpinner.toString().contains(mBaseCurrency) && listForLV.contains(mBaseCurrency)
        ) {
            val spinnerIndex = listForSpinner.indices.find { listForSpinner[it] == mBaseCurrency }
            val listIndex = listForLV.indices.find { listForLV[it] == mBaseCurrency }
            spinnerIndex?.let { listForSpinner.removeAt(it) }
            listIndex?.let { listForLV.removeAt(it) }
        }
        //Remove "Currency" value from the list, that is intended for ListView
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
                    Log.i(TAG, "onNothingSelected in historical spinner ")
                }
            }
    }

    //Prepare ListView to display available currencies to pick up, as a reference to historical rates of selected base currency
    private fun setupListView(list: MutableList<String>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)


        mBinding.historicalSymbolsLv.adapter = adapter
        mBinding.historicalSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
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

            val selectedCurrencies: MutableList<String> = mutableListOf()

            // clear list after reset
            if (mIsRefreshed) {
                selectedCurrencies.clear()
            }

            /** Add to the created list all of the checked symbols. Next function will convert them into String */
            for (i in 0 until list.size) {
                if (mBinding.historicalSymbolsLv.isItemChecked(i)) {
                    selectedCurrencies.add(list[i])
                }
            }
            getCurrencies(selectedCurrencies)
        }
    }

    /*  Fetch data from the api, and observe it.
     After whole data is fetched, pass it into adapter which will display it in RecyclerView    */
    private fun getCurrencies(list: MutableList<String>) {
        if (mBaseCurrency != "default" && mDate != "default") {
            mViewModel.fetchHistoricalData(
                baseCurrency = mBaseCurrency,
                selectedCurrencies = list.joinToString(separator = ", "), date = mDate
            )
            prepareViewsForRv()
        }
    }

    //  Prepare views to display RecyclerView. Delete unneeded views
    private fun prepareViewsForRv() {
        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
        mBinding.historicalChangeBase.visibility = View.INVISIBLE

        mBinding.historicalDateTv.visibility = View.VISIBLE
        mBinding.historicalRv.visibility = View.VISIBLE
    }
}

