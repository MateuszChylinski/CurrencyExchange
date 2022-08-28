package com.example.currencyexchange.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel

class HistoricalRates : Fragment() {
//    VARIABLES
    private var mInfo: TextView? = null
    private var mSelectionInfo: TextView? = null
    private var mChangeInfo: TextView? = null
    private var mBaseCurrency: TextView? = null
    private var mDate: TextView? = null

    private var mSaveDate: Button? = null
    private var mSaveSymbols: Button? = null

    private var mDatePicker: DatePicker? = null
    private var mSymbols: ListView? = null
    private var mChangeBase: Spinner? = null
    private var mSymbolsRv: RecyclerView? = null


//    VIEWS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {return inflater.inflate(R.layout.fragment_historical_rates, container, false)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mInfo = view.findViewById(R.id.historical_info)
        mSelectionInfo = view.findViewById(R.id.historical_select_info)
        mChangeInfo = view.findViewById(R.id.historical_change_info)
        mBaseCurrency = view.findViewById(R.id.historical_base_tv)
        mDate = view.findViewById(R.id.historical_date_tv)
        mSaveDate = view.findViewById(R.id.historical_save_date)
        mSaveSymbols = view.findViewById(R.id.historical_save_symbols)
        mDatePicker = view.findViewById(R.id.historical_dt)
        mSymbols = view.findViewById(R.id.historical_symbols_lv)
        mChangeBase = view.findViewById(R.id.historical_change_base)
        mSymbolsRv = view.findViewById(R.id.historical_rv)
    }
}