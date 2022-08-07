package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.FluctuationAdapter
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyRetrofitRepository
import com.example.currencyexchange.ViewModels.CurrencyRetrofitViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class Fluctuation : Fragment() {
    var baseCurrencyTV: TextView? = null

    //  FROM
    var fromTV: TextView? = null
    var fromDateTV: TextView? = null
    var fromSetDate: Button? = null
    var fromCenterTV: TextView? = null
    var fromDatePicker: DatePicker? = null
    var fromCancel: Button? = null
    var fromOk: Button? = null

    private var mFromFullDate: String = "default"

    //  TO
    var toTV: TextView? = null
    var toDateTV: TextView? = null
    var toSetDate: Button? = null
    var toCenterTV: TextView? = null
    var toDatePicker: DatePicker? = null
    var toCancel: Button? = null
    var toOk: Button? = null

    private var mToFullDate: String = "default"


    private var mRecyclerView: RecyclerView? = null
    private var mFluctuationAdapter: FluctuationAdapter? = null

    private val currentCal = Calendar.getInstance()
    private val minimalCal = Calendar.getInstance()

    private val mRetrofitServices = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyRetrofitViewModel

    private val mCurrenciesNames: MutableList<String> = arrayListOf()
    private val mCurrenciesStartRate: MutableList<Double> = arrayListOf()
    private val mCurrenciesEndRate: MutableList<Double> = arrayListOf()
    private val mCurrenciesChange: MutableList<Double> = arrayListOf()
    private val mCurrenciesChangePct: MutableList<Double> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fluctuation, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseCurrencyTV = view.findViewById(R.id.fluctuation_base)
        baseCurrencyTV?.visibility = View.INVISIBLE

        fromTV = view.findViewById(R.id.fluctuation_from_tv)
        fromDateTV = view.findViewById(R.id.fluctuation_from_date)
        fromSetDate = view.findViewById(R.id.fluctuation_set_from)
        fromCenterTV = view.findViewById(R.id.fluctuation_from_center_tv)
        fromDatePicker = view.findViewById(R.id.fluctuation_from_dt)
        fromCancel = view.findViewById(R.id.fluctuation_set_from_cancel)
        fromOk = view.findViewById(R.id.fluctuation_set_from_ok)

        toTV = view.findViewById(R.id.fluctuation_to_tv)
        toDateTV = view.findViewById(R.id.fluctuation_to_date)
        toSetDate = view.findViewById(R.id.fluctuation_set_to)
        toCenterTV = view.findViewById(R.id.fluctuation_to_center_tv)
        toDatePicker = view.findViewById(R.id.fluctuation_to_dt)
        toCancel = view.findViewById(R.id.fluctuation_set_to_cancel)
        toOk = view.findViewById(R.id.fluctuation_set_to_ok)
        mRecyclerView = view.findViewById(R.id.fluctuation_rv)

        setupDatePicker()

        fromSetDate?.setOnClickListener {
            setVisibilityFrom()
        }
        toSetDate?.setOnClickListener {
            setVisibilityTo()
        }
    }

    private fun setVisibilityFrom() {
        toCenterTV?.visibility = View.INVISIBLE
        toDatePicker?.visibility = View.INVISIBLE
        toCancel?.visibility = View.INVISIBLE
        toOk?.visibility = View.INVISIBLE

        fromCenterTV?.visibility = View.VISIBLE
        fromDatePicker?.visibility = View.VISIBLE
        fromCancel?.visibility = View.VISIBLE
        fromOk?.visibility = View.VISIBLE
        fromOk?.setOnClickListener {

            getDateFromUser(1)
            setVisibilityTo()
        }

    }

    private fun setVisibilityTo() {
        toCenterTV?.visibility = View.VISIBLE
        toDatePicker?.visibility = View.VISIBLE
        toCancel?.visibility = View.VISIBLE
        toOk?.visibility = View.VISIBLE

        toOk?.setOnClickListener {
            getDateFromUser(2)
            destroyViews()
        }

        fromCenterTV?.visibility = View.INVISIBLE
        fromDatePicker?.visibility = View.INVISIBLE
        fromCancel?.visibility = View.INVISIBLE
        fromOk?.visibility = View.INVISIBLE

    }

    private fun setupDatePicker() {
//      minimal possible date to execute: 1999-02-01 YYYY/MM/DD.
        minimalCal.set(1999, 2, 1)
        fromDatePicker?.minDate = minimalCal.timeInMillis
        toDatePicker?.minDate = minimalCal.timeInMillis


        fromDatePicker?.maxDate = currentCal.timeInMillis
        toDatePicker?.maxDate = currentCal.timeInMillis
    }

    private fun getDateFromUser(selection: Int?) {
//      1 - from / 2 - to
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

    private fun destroyViews() {
        fromTV?.visibility = View.GONE
        fromDateTV?.visibility = View.GONE
        fromSetDate?.visibility = View.GONE
        fromCenterTV?.visibility = View.GONE
        fromDatePicker?.visibility = View.GONE
        fromCancel?.visibility = View.GONE
        fromOk?.visibility = View.GONE
        //  TO
        toTV?.visibility = View.GONE
        toDateTV?.visibility = View.GONE
        toSetDate?.visibility = View.GONE
        toCenterTV?.visibility = View.GONE
        toDatePicker?.visibility = View.GONE
        toCancel?.visibility = View.GONE
        toOk?.visibility = View.GONE

        prepareRecyclerView()
        getFluctuationData()
    }

    private fun prepareRecyclerView() {
        baseCurrencyTV?.visibility = View.VISIBLE
        mRecyclerView?.layoutManager = LinearLayoutManager(this.requireContext())
        mFluctuationAdapter = FluctuationAdapter()
    }


    private fun getFluctuationData() {
        mViewModel = ViewModelProvider(
            this,
            CurrencyViewModelFactory(CurrencyRetrofitRepository(mRetrofitServices))
        )
            .get(CurrencyRetrofitViewModel::class.java)

        mViewModel.fetchFluctuation(mFromFullDate, mToFullDate, "PLN")
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

            Log.i(TAG, "getFluctuationData: ${it.startDate} ---  ${it.endDAte}")
        })
    }
}
//TODO Make a settings menu with possibility to change the base currency / make a call with some n-length max symbols.