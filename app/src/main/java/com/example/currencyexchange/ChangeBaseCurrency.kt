package com.example.currencyexchange

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel

class ChangeBaseCurrency : Fragment() {
    //  TAG
    private var TAG = "ChangeBaseCurrency"

    //  Views
    private var mCurrentBaseCurrency: TextView? = null
    private var mSelectNewBaseCurrency: Spinner? = null

    //  Variables
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }
    private val mBaseCurrency = StringBuilder()
    private var mAllCurrencyNames: MutableList<CurrencyNamesModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_base_currency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mCurrentBaseCurrency = view.findViewById(R.id.change_base_current_base)
        mSelectNewBaseCurrency = view.findViewById(R.id.change_base_select_currency_spinner)

        getBaseCurrency()
    }

    private fun getBaseCurrency() {
        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
            mBaseCurrency.append(it).toString()
        })
        if (!mBaseCurrency.isEmpty()){
            getAllCurrencies()
        }
    }

    // TODO - check case when the basic currency will be for example 'EUR', so it'll be deleted from the list in this fun, because you can't set as base 'EUR' when you already have it as a base. So, whenever user change from 'EUR', to some different base, let's say 'USD', and then he'd like to back to the 'EUR' is this currency will be still available?
    private fun getAllCurrencies() {
        mDatabaseViewModel.allCurrencies.observe(requireActivity(), Observer {
            mAllCurrencyNames.addAll(it)
            deleteBaseFromList()
        })
    }

    //  Delete currency name that is same as the base  
    private fun deleteBaseFromList() {
        for (i in 0 until mAllCurrencyNames.size-1) {
           if (mAllCurrencyNames[i].toString().equals(mBaseCurrency)){
               mAllCurrencyNames.removeAt(i)
           }
        }
        prepareSpinner(mAllCurrencyNames)
    }

    private fun prepareSpinner(list: List<CurrencyNamesModel>) {
        val spinnerAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item, list
        )
        mSelectNewBaseCurrency?.adapter = spinnerAdapter
        mSelectNewBaseCurrency?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    updateBaseCurrency(mAllCurrencyNames[p2].toString())
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: ")
                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Log.i(TAG, "onItemClick: INDEX = $p2  | VALUE = " + mAllCurrencyNames[p2])
                }
            }
    }

//  TODO - WHY I AM GETTING ITEM SELECTED, BUT I HAVEN'T SELECTED ANY YET?
    private fun updateBaseCurrency(selectedCurrency: String){
        Log.i(TAG, "updateBaseCurrency: $selectedCurrency")
    }
}
