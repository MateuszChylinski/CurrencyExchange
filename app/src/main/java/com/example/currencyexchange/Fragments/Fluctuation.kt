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
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentFluctuationBinding
import java.text.SimpleDateFormat
import java.util.*

class Fluctuation : Fragment() {
    private val TAG = "Fluctuation"
    private val mYearInMilis = 31556926000
    private val mDayInMilis = 86400000

    // VARIABLES
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private var mConcatenatedSymbols: String = ""
    private var mBaseCurrency: String = "default"

    private var mIsSpinnerInit = false

    private var mAllCurrencies: MutableList<CurrencyNamesModel> = arrayListOf()
    private var mCurrencyList: MutableList<CurrencyNamesModel> = arrayListOf()

    private var currenciesNames: MutableList<String> = arrayListOf()
    private var currenciesStartRates: MutableList<Double> = arrayListOf()
    private var currenciesEndRates: MutableList<Double> = arrayListOf()
    private var currenciesChange: MutableList<Double> = arrayListOf()
    private var currenciesChangePct: MutableList<Double> = arrayListOf()

    @SuppressLint("SimpleDateFormat")
    private val mSdf = SimpleDateFormat("yyyy-MM-dd")
    val mCalendar = Calendar.getInstance()

    //  View model
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

        mViewModel.baseCurrency.observe(requireActivity(), androidx.lifecycle.Observer { mBaseCurrency = it })
        mViewModel.allCurrencies.observe(requireActivity(), androidx.lifecycle.Observer {
            mAllCurrencies.addAll(it)
            mCurrencyList.addAll(it)
        })
        mViewModel.currenciesNames.observe(requireActivity(), androidx.lifecycle.Observer { currenciesNames.add(it) })
        mViewModel.currenciesStartRates.observe(requireActivity(), androidx.lifecycle.Observer { currenciesStartRates.addAll(listOf(it)) })
        mViewModel.currenciesEndRates.observe(requireActivity(), androidx.lifecycle.Observer { currenciesEndRates.addAll(listOf(it)) })
        mViewModel.currenciesChange.observe(requireActivity(), androidx.lifecycle.Observer { currenciesChange.addAll(listOf(it)) })
        mViewModel.currenciesChangePct.observe(requireActivity(), androidx.lifecycle.Observer { currenciesChangePct.addAll(listOf(it)) })
        mViewModel.isDone.observe(requireActivity(), Observer {
            when (it) {
                true -> {
                    mFluctuationAdapter = FluctuationAdapter()
                    mFluctuationAdapter?.setData(
                        currenciesNames,
                        currenciesStartRates,
                        currenciesEndRates,
                        currenciesChange,
                        currenciesChangePct
                    )
                    prepareRecyclerView()
                }
                else -> Log.i(TAG, "getCurrencies: MISSING DATA?")
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * Setup date picker where user can set from where he want to display fluctuation from.
         * Since the API allow to see fluctuation back to 365 days, the minimum date is set to display max a year before actual date
         */
        mBinding.fluctuationFromDt.minDate = Calendar.getInstance().timeInMillis - mYearInMilis
        mBinding.fluctuationFromDt.maxDate = Calendar.getInstance().timeInMillis

        mBinding.fluctuationSetFromOk.setOnClickListener {
            getDateFromUser(1)
            setupViewsToGetDate()
        }
        mBinding.fluctuationSetToOk.setOnClickListener {
            getDateFromUser(2)
            setupViewsForListView()
        }
    }

    //  Fun that providing selected by user date into ViewModel.
    @SuppressLint("SimpleDateFormat")
    private fun getDateFromUser(selection: Int) {
        when (selection) {
            1 -> {
                mCalendar.set(Calendar.YEAR, mBinding.fluctuationFromDt.year)
                mCalendar.set(Calendar.MONTH, mBinding.fluctuationFromDt.month)
                mCalendar.set(Calendar.DAY_OF_MONTH, mBinding.fluctuationFromDt.dayOfMonth)
                mViewModel.startDate = mSdf.format(mCalendar.time).toString()

//              After picking starting date, set "ending" date picker. As a minimum date, provide the selected earlier minimum date+1 day.
                mBinding.fluctuationToDt.minDate = (mCalendar.timeInMillis + mDayInMilis)
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
        mBinding.fluctuationFromCenterTv.visibility = View.GONE
        mBinding.fluctuationFromDt.visibility = View.GONE
        mBinding.fluctuationSetFromOk.visibility = View.GONE

        mBinding.fluctuationToCenterTv.visibility = View.VISIBLE
        mBinding.fluctuationToDt.visibility = View.VISIBLE
        mBinding.fluctuationSetToOk.visibility = View.VISIBLE
    }

    private fun setupViewsForListView() {
        mBinding.fluctuationToCenterTv.visibility = View.GONE
        mBinding.fluctuationToDt.visibility = View.GONE
        mBinding.fluctuationSetToOk.visibility = View.GONE

        mBinding.fluctuationSelectBaseCurrency.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.fluctuationBaseCurrencyTv.text =
            String.format("Base Currency: %s", mBaseCurrency)

        mBinding.fluctuationFromDate.visibility = View.VISIBLE
        mBinding.fluctuationToDate.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.VISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.VISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.VISIBLE

        mBinding.fluctuationFromDate.text = String.format("From: %s", mViewModel.startDate)
        mBinding.fluctuationToDate.text = String.format("To: %s", mViewModel.endDate)

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
                            String.format("Base Currency: %s", mBaseCurrency)

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

    private fun setupListView(list: MutableList<CurrencyNamesModel>) {
        val symbols: MutableList<CurrencyNamesModel> = mutableListOf()
        val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)

        Toast.makeText(activity, "Select up to 30 currencies, and then, click on the save button", Toast.LENGTH_SHORT).show()

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
                    Toast.makeText(requireContext(), "You can't select anymore currencies.", Toast.LENGTH_SHORT).show()
                    mBinding.fluctuationSelectSymbolsLv.setItemChecked(position, false)
                }
                Log.i(TAG, "onItemClick: "+mBinding.fluctuationSelectSymbolsLv.checkedItemIds)
            }
        }
        mBinding.fluctuationSaveSymbols.setOnClickListener {
//          Add to the created list all of the checked symbols. Next function will convert them into String
            for (i in 0 until list.size){
                if (mBinding.fluctuationSelectSymbolsLv.isItemChecked(i)){symbols.add(list[i])}
            }
            getCurrencies(symbols)
        }
    }

    private fun getCurrencies(list: MutableList<CurrencyNamesModel>) {
        for (i in 0 until list.size) {
                mConcatenatedSymbols += list[i].toString() + ", "
        }
//      Pass converted symbols from list to String to the ViewModel. These symbol will be one of the endpoints needed to perform the call
        mViewModel.selectedCurrencies = mConcatenatedSymbols
        //  Fetch data from the api, and observe it. After whole data is fetched, pass it into adapter which will display it in RecyclerView
        mViewModel.fetchFluctuation(mBaseCurrency)
    }

    private fun prepareRecyclerView() {
        mBinding.fluctuationBaseCurrencyTv.text = mBaseCurrency

        mBinding.fluctuationBaseCurrencyTv.visibility = View.VISIBLE
        mBinding.fluctuationRv.visibility = View.VISIBLE
        mBinding.fluctuationBaseInRv.visibility = View.VISIBLE
        mBinding.fluctuationBaseInRv.text = String.format("Base currency: %s", mBaseCurrency)

        mBinding.fluctuationRv.layoutManager = LinearLayoutManager(this.requireContext())
        mBinding.fluctuationRv.adapter = mFluctuationAdapter

        mBinding.fluctuationSelectBaseCurrency.visibility = View.INVISIBLE
        mBinding.fluctuationBaseCurrencyTv.visibility = View.INVISIBLE

        mBinding.fluctuationFromDate.visibility = View.INVISIBLE
        mBinding.fluctuationToDate.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsLv.visibility = View.INVISIBLE
        mBinding.fluctuationSelectSymbolsTv.visibility = View.INVISIBLE
        mBinding.fluctuationSaveSymbols.visibility = View.INVISIBLE
    }
}
