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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

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
    private var mBaseCurrency: String = ""
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

//  Retrieve base currency via ViewModel for database
    private fun getBaseCurrency() {
        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
            mBaseCurrency = it.toString()
            mCurrentBaseCurrency?.text = String.format("Current base: %s", mBaseCurrency)
        })
        getAllCurrencies()
    }

//  Retrieve all currencies that API contains via database ViewModel
    private fun getAllCurrencies() {
        mDatabaseViewModel.allCurrencies.observe(requireActivity(), Observer {
            mAllCurrencyNames.addAll(it)
            deleteBaseFromList()
        })
    }

    //  Delete currency name that is same as the base  
    private fun deleteBaseFromList() {
        for (i in 0 until mAllCurrencyNames.size - 1) {
            if (mAllCurrencyNames[i].toString().equals(mBaseCurrency)) {
                mAllCurrencyNames.removeAt(i)
            }
        }
        prepareSpinner(mAllCurrencyNames)
    }

//  Make basic setup for spinner. Initiate adapter with list. Implement OnItemClickListener to get clicked by user currency, and then update base currency in database
    private fun prepareSpinner(list: List<CurrencyNamesModel>) {
        val spinnerAdapter = ArrayAdapter(
            requireActivity(), android.R.layout.simple_spinner_item, list
        )
        mSelectNewBaseCurrency?.adapter = spinnerAdapter
//      To avoid item selection on start of the fragment.
        mSelectNewBaseCurrency?.setSelection(0, false)
        mSelectNewBaseCurrency?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    Log.i(TAG, "onItemSelected: "+mAllCurrencyNames[p2])
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

//  Update base currency
    private fun updateBaseCurrency(selectedCurrency: String) {
        val newBase = BaseCurrencyModel(selectedCurrency)
        Log.i(TAG, "updateBaseCurrency: $selectedCurrency || $newBase")

        CoroutineScope(Dispatchers.IO).launch {
            mDatabaseViewModel.updateCurrency(newBase)
        }
    }
}
