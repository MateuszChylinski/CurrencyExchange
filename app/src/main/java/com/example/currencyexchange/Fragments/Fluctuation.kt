package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.os.bundleOf
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentFluctuationBinding
import java.text.SimpleDateFormat
import java.util.*

class Fluctuation : Fragment() {
    private val TAG = "Fluctuation"
    private val mYearInMs = 31556926000
    private val mDayInMs = 86400000

    // VARIABLES
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private var mBaseCurrency: String = "default"

    private var mIsSpinnerInit = false
    private var mIsLayoutRefreshed: Boolean = false

    private var mAllCurrencies: MutableList<CurrencyNamesModel> = arrayListOf()
    private var mCurrencyList: MutableList<CurrencyNamesModel> = arrayListOf()

    @SuppressLint("SimpleDateFormat")
    private val mSdf = SimpleDateFormat("yyyy-MM-dd")
    private val mCalendar = Calendar.getInstance()

    //  ViewModel
    private val mApiInstance = ApiServices.getInstance()
    private var mDatabaseInstance: CurrencyDatabaseRepository? = null
    private lateinit var mViewModel: FluctuationViewModel

    //  View binding
    private var _binding: FragmentFluctuationBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFluctuationBinding.inflate(inflater, container, false)
        val view = mBinding.root

        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
        mViewModel = ViewModelProvider(
            this,
            FluctuationFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
        ).get(FluctuationViewModel::class.java)

        mViewModel.baseCurrency.observe(
            requireActivity(),
            androidx.lifecycle.Observer { mBaseCurrency = it })
        mViewModel.allCurrencies.observe(requireActivity(), androidx.lifecycle.Observer {
            mAllCurrencies.addAll(it)
            mCurrencyList.addAll(it)
        })
        mViewModel.data.observe(requireActivity(), Observer {
            mFluctuationAdapter = FluctuationAdapter()

            mFluctuationAdapter?.setData(it ?: mapOf())
            mBinding.fluctuationRv.layoutManager = LinearLayoutManager(this.requireContext())
            mBinding.fluctuationRv.adapter = mFluctuationAdapter
        })

//      Refresh fragment
        mBinding.fluctuationRefreshContainer.setOnRefreshListener {
            mIsLayoutRefreshed = true
            mViewModel.clearResponseData()
            mBaseCurrency = mViewModel.getBaseCurrency()

            defaultViewsSetup()
            mBinding.fluctuationRefreshContainer.isRefreshing = false
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * Setup date picker where user can set from where he want to display fluctuation from.
         * Since the API allow to see fluctuation back to 365 days, the minimum date is set to display max a year before actual date
         */
        mBinding.fluctuationFromDt.minDate = Calendar.getInstance().timeInMillis - mYearInMs
        mBinding.fluctuationFromDt.maxDate = Calendar.getInstance().timeInMillis

        mBinding.fluctuationSetFromOk.setOnClickListener {
            getDateFromUser(1)
            setupViewsToGetDate()
        }
        mBinding.fluctuationSetToOk.setOnClickListener {
            getDateFromUser(2)
            setupViewsForListView()
        }
        mBinding.fluctuationChangeBaseCurrency.setOnClickListener {
            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
            findNavController().navigate(R.id.action_from_base_to_change)
        }
    }

    /** When user will refresh layout, by using the SwiperRefreshLayout, this function will be called.
     *  It'll reset the UI to the "default" state. As it was when user entered the fragment for the first time*/
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

        //Uncheck previously checked by user currencies.
        for (i in 0 until mBinding.fluctuationSelectSymbolsLv.size) {
            if (mBinding.fluctuationSelectSymbolsLv.isItemChecked(i)) {
                mBinding.fluctuationSelectSymbolsLv.setItemChecked(i, false)
            }
        }

        mBinding.fluctuationFromCenterTv.visibility = View.VISIBLE
        mBinding.fluctuationFromDt.visibility = View.VISIBLE
        mBinding.fluctuationSetFromOk.visibility = View.VISIBLE
    }

    /** Provide data from DatePicker to the ViewModel.
     * Later on, these values can be useful for displaying picked date, and performing api call*/
    @SuppressLint("SimpleDateFormat")
    private fun getDateFromUser(selection: Int) {
        /* Selection stands for DatePicker.
           Since there will be two DatePickers, the first one (start date) will be marked as '1'.
           The second (end date) will be marked as '2'   */
        when (selection) {
            1 -> {
                mCalendar.set(Calendar.YEAR, mBinding.fluctuationFromDt.year)
                mCalendar.set(Calendar.MONTH, mBinding.fluctuationFromDt.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.fluctuationFromDt.dayOfMonth)
                mViewModel.startDate = mSdf.format(mCalendar.time).toString()

//              After picking starting date, set "ending" date picker. As a minimum date, provide the selected earlier minimum date+1 day.
                mBinding.fluctuationToDt.minDate = (mCalendar.timeInMillis + mDayInMs)
                mBinding.fluctuationToDt.maxDate = Calendar.getInstance().timeInMillis
            }
            2 -> {
                mCalendar.set(Calendar.YEAR, mBinding.fluctuationToDt.year)
                mCalendar.set(Calendar.MONTH, mBinding.fluctuationToDt.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.fluctuationToDt.dayOfMonth)
                mViewModel.endDate = mSdf.format(mCalendar.time).toString()
            }
        }
    }

    private fun setupViewsToGetDate() {
        mBinding.fluctuationFromCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationFromDt.visibility = View.INVISIBLE
        mBinding.fluctuationSetFromOk.visibility = View.INVISIBLE

        mBinding.fluctuationToCenterTv.visibility = View.VISIBLE
        mBinding.fluctuationToDt.visibility = View.VISIBLE
        mBinding.fluctuationSetToOk.visibility = View.VISIBLE
    }

    private fun setupViewsForListView() {
        mBinding.fluctuationToCenterTv.visibility = View.INVISIBLE
        mBinding.fluctuationToDt.visibility = View.INVISIBLE
        mBinding.fluctuationSetToOk.visibility = View.INVISIBLE

        mBinding.fluctuationSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)

        mBinding.fluctuationFromDate.visibility = View.VISIBLE
        mBinding.fluctuationToDate.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.VISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.VISIBLE

        mBinding.fluctuationFromDate.text =
            String.format(getString(R.string.formatted_from), mViewModel.startDate)
        mBinding.fluctuationToDate.text =
            String.format(getString(R.string.formatted_to), mViewModel.endDate)

        if (mBaseCurrency != "default") {
            deleteBaseCurrencyFromList(mAllCurrencies)
        }
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

    /** Setup spinner that allow user to change the base currency. It will NOT affect the database, it will be a temporary change. */
    private fun setupBaseCurrencySpinner(currencyNames: MutableList<CurrencyNamesModel>) {
        var mIsTouched = false

        val mSpinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyNames)
        mBinding.fluctuationSelectBaseCurrency.adapter = mSpinnerAdapter
        mBinding.fluctuationSelectBaseCurrency.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (mIsTouched) {
                        mBaseCurrency = currencyNames[p2].toString()
                        mBinding.fluctuationBaseCurrencyTv.text =
                            String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )

                        currencyNames.clear()
                        currencyNames.addAll(mCurrencyList)
                        deleteBaseCurrencyFromList(currencyNames)
                        setupListView(currencyNames)
                        mSpinnerAdapter.notifyDataSetChanged()
                    } else {
                        mIsTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: IN FLUCTUATION SPINNER")
                }
            }
    }

    /** Setup ListView, so user can pick currencies, that he would like to compare with base currency*/
    private fun setupListView(list: MutableList<CurrencyNamesModel>) {
        val symbols: MutableList<CurrencyNamesModel> = mutableListOf()
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)

        Toast.makeText(
            activity,
            getString(R.string.select_up_to_30_currencies),
            Toast.LENGTH_SHORT
        ).show()

        mBinding.fluctuationSelectSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mBinding.fluctuationSelectSymbolsLv.adapter = adapter
        mBinding.fluctuationSelectSymbolsLv.onItemClickListener = object : OnItemClickListener {
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
            // Whenever layout will be refreshed, clear the symbols list, so it will not contain previously picked currencies.
            if (mIsLayoutRefreshed) {
                symbols.clear()
            }

            //Add to the created list all of the checked symbols. Next function will convert them into String
            for (i in 0 until list.size) {
                if (mBinding.fluctuationSelectSymbolsLv.isItemChecked(i)) {
                    symbols.add(list[i])
                }
            }
            getCurrencies(symbols)
        }
    }

    private fun getCurrencies(list: MutableList<CurrencyNamesModel>) {
        //Perform an api call by given base currency, and checked currencies from the ListView, and change views visibility, to display RecyclerView.
        mViewModel.fetchFluctuation(mBaseCurrency, list.joinToString(separator = ", "))
        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {
        mBinding.fluctuationBaseCurrencyTv.text = mBaseCurrency
        mBinding.fluctuationBaseInRv.text =
            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)

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
