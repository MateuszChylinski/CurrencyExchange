package com.example.currencyexchange.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.HistoricalAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.HistoricalFactory
import com.example.currencyexchange.ViewModels.HistoricalViewModel
import com.example.currencyexchange.databinding.FragmentHistoricalRatesBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoricalRates : Fragment() {
    //  View Binding
    private var _binding: FragmentHistoricalRatesBinding? = null
    private val mBinding get() = _binding!!

    // Instances
    private val mApiInstance = ApiServices.getInstance()
    private var mDatabaseInstance: CurrencyDatabaseRepository? = null

    //  View Model
    lateinit var mViewModel: HistoricalViewModel

    //  Variables
    private val TAG = "HistoricalRates"
    private var mConcatenatedSymbols: String = ""
    private var mDate: String = "default"
    private var mBaseCurrency = "default"
    private var mIsInit = false
    private var mIsTouched = false
    private var mHistoricalAdapter: HistoricalAdapter? = null

    private val mCalendar = Calendar.getInstance()
    private var mCurrencyList: MutableList<CurrencyNamesModel> = arrayListOf()
    private var mAlLCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoricalRatesBinding.inflate(inflater, container, false)
        val view = mBinding.root

        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
        mViewModel = ViewModelProvider(
            this,
            HistoricalFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
        ).get(HistoricalViewModel::class.java)

        mViewModel.baseCurrency.observe(
            requireActivity(),
            androidx.lifecycle.Observer { mBaseCurrency = it })
        mViewModel.currencyList.observe(requireActivity(), androidx.lifecycle.Observer {
            mCurrencyList.addAll(it)
            mAlLCurrencies.addAll(it)
        })
        mViewModel.historicalData.observe(requireActivity(), androidx.lifecycle.Observer {
            mHistoricalAdapter = HistoricalAdapter()
            mHistoricalAdapter?.setData(it.rates)
            mBinding.historicalRv.layoutManager = LinearLayoutManager(this.context)
            mBinding.historicalRv.adapter = mHistoricalAdapter
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCalendar.set(1999, 2, 1)
        mBinding.historicalDt.minDate = mCalendar.timeInMillis
        mBinding.historicalDt.maxDate = Calendar.getInstance().timeInMillis

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
        mViewModel.date = mDate

        setVisibilityToLv()
    }

    //  Prepare views for ListView. Delete unneeded views.
    private fun setVisibilityToLv() {
        mBinding.historicalInfo.visibility = View.GONE
        mBinding.historicalDt.visibility = View.GONE
        mBinding.historicalSaveDate.visibility = View.GONE

        mBinding.historicalSelectInfo.visibility = View.VISIBLE
        mBinding.historicalSymbolsLv.visibility = View.VISIBLE
        mBinding.historicalSaveSymbols.visibility = View.VISIBLE
        mBinding.historicalChangeInfo.visibility = View.VISIBLE
        mBinding.historicalChangeBase.visibility = View.VISIBLE
        mBinding.historicalBaseTv.visibility = View.VISIBLE
        mBinding.historicalDateTv.visibility = View.VISIBLE

        mBinding.historicalDateTv.text = String.format("Date: %s", mDate)
        mBinding.historicalBaseTv.text = String.format("Base currency: %s", mBaseCurrency)

        deleteBaseFromTheList(mCurrencyList)
    }

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
        mBinding.historicalChangeBase.adapter = adapter
        mBinding.historicalChangeBase.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (mIsTouched) {
                        mBaseCurrency = currencies[p2].toString()
                        mBinding.historicalBaseTv.text =
                            String.format("Base currency: %s", mBaseCurrency)
                        currencies.clear()
                        currencies.addAll(mAlLCurrencies)
                        deleteBaseFromTheList(currencies)
                        setupListView(currencies)
                        adapter.notifyDataSetChanged()
                    } else {
                        mIsTouched = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected in historical spinner ")
                }
            }
    }

    private fun setupListView(list: MutableList<CurrencyNamesModel>) {
        val currencies: MutableList<CurrencyNamesModel> = mutableListOf()
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)

        Toast.makeText(
            activity,
            "Select up to 30 currencies, and then, click on the save button",
            Toast.LENGTH_SHORT
        ).show()

        mBinding.historicalSymbolsLv.adapter = adapter
        mBinding.historicalSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        mBinding.historicalSymbolsLv.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                /** After every click, check if total selected amount of symbols is <= 30.
                //  If user will try to select more than 30 symbols, inform him that he can't select more than 30 **/

                if (mBinding.historicalSymbolsLv.checkedItemCount > 30) {
                    Toast.makeText(
                        requireContext(),
                        "You can't select anymore currencies.",
                        Toast.LENGTH_SHORT
                    ).show()
                    mBinding.historicalSymbolsLv.setItemChecked(position, false)
                }
            }
        }
        mBinding.historicalSaveSymbols.setOnClickListener {
            //          Add to the created list all of the checked symbols. Next function will convert them into String
            for (i in 0 until list.size) {
                if (mBinding.historicalSymbolsLv.isItemChecked(i)) {
                    currencies.add(list[i])
                }
            }
            getCurrencies(currencies)
        }
    }


    private fun getCurrencies(list: MutableList<CurrencyNamesModel>) {
        for (i in 0 until list.size) {
            mConcatenatedSymbols += list[i].toString() + ", "
        }
//      Pass converted symbols list to the ViewModel as a String. These symbols will be one of the endpoints needed to perform the call
        mViewModel.selectedCurrencies = mConcatenatedSymbols
        prepareViewsForRv()

        //  Fetch data from the api, and observe it. After whole data is fetched, pass it into adapter which will display it in RecyclerView
        mViewModel.fetchHistoricalData(mBaseCurrency)
    }

    //  Prepare views to display RecyclerView. Delete unneeded views
    private fun prepareViewsForRv() {
        mBinding.historicalSelectInfo.visibility = View.GONE
        mBinding.historicalSymbolsLv.visibility = View.GONE
        mBinding.historicalSaveSymbols.visibility = View.GONE
        mBinding.historicalChangeInfo.visibility = View.GONE
        mBinding.historicalChangeBase.visibility = View.GONE

        mBinding.historicalDateTv.visibility = View.VISIBLE
        mBinding.historicalRv.visibility = View.VISIBLE
    }
}

