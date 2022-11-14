package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.Adapters.PagerAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.BaseCurrencyModel
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel
import kotlinx.coroutines.NonDisposableHandle.parent

class ChangeBaseCurrency : Fragment() {
    //  TAG
    private var TAG = "ChangeBaseCurrency"

    val args: ChangeBaseCurrencyArgs by navArgs()

    //  Views
    private var mCurrentBaseCurrency: TextView? = null
    private var mSelectNewBaseCurrency: Spinner? = null

    //  Variables
    private val mDatabaseViewModel: CurrencyDatabaseViewModel by activityViewModels {
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }
    private var mBaseCurrency: String = "default"
    private var mAllCurrencyNames: MutableList<CurrencyNamesModel> = mutableListOf()
    private var mIsInit = false
    private var mFragmentName: String? = "def"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_change_base_currency, container, false)
        mFragmentName = args.fragmentName
        Log.i(TAG, "onCreateView: ||||||||||||||||||||||||||||||||||||| $mFragmentName")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mCurrentBaseCurrency = view.findViewById(R.id.change_base_current_base)
        mSelectNewBaseCurrency = view.findViewById(R.id.change_base_select_currency_spinner)

//        getCurrencies()
    }

    //  Retrieve base currency, and all currency names from database via view model
//    private fun getCurrencies() {
//        mDatabaseViewModel.baseCurrency.observe(requireActivity(), Observer {
//            mBaseCurrency = it.toString()
//            mCurrentBaseCurrency?.text = String.format("Base currency: %s", mBaseCurrency)
//        })
//        mDatabaseViewModel.currencyNames.observe(requireActivity(), Observer {
//            mAllCurrencyNames.addAll(it)
//            if (mBaseCurrency != "default") {
//                deleteBaseFromList(it as MutableList<CurrencyNamesModel>)
//            }
//        })
//    }

    //  Look for base currency in given list, and delete it.
    private fun deleteBaseFromList(list: MutableList<CurrencyNamesModel>) {
        if (list.toString().contains(mBaseCurrency)) {
            val index = list.indices.find { list[it].toString() == mBaseCurrency }
            list.removeAt(index!!)
        }
// To avoid infinite loop in spinner's 'onItemSelected' set mIsInit flag.
        if (!mIsInit) {
            mIsInit = true
            setupSpinner(list)

        }
    }

    //  Prepare spinner, fill with data without base currency. To avoid selecting by program the first value from the spinner, I've used the 'setSelection'
    private fun setupSpinner(list: MutableList<CurrencyNamesModel>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, list)
        mSelectNewBaseCurrency?.adapter = adapter
        mSelectNewBaseCurrency?.setSelection(0, false)
        mSelectNewBaseCurrency?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    mBaseCurrency = list[p2].toString()

                    updateBaseCurrency(mBaseCurrency)

                    list.clear()
                    list.addAll(mAllCurrencyNames)
                    deleteBaseFromList(list)
                    adapter.notifyDataSetChanged()

                    moveToPreviousFragment()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: ")
                }
            }
    }

    private fun moveToPreviousFragment() {
        findNavController().navigate(R.id.action_changeBaseCurrency_to_pagerBase)
    }
//        when (mFragmentName) {
//            "Latest" -> {
//                findNavController().navigate(R.id.action_from_change_to_latest)
//            }
//            "Fluctation" -> {
//                findNavController().navigate(R.id.action_from_change_to_fluctuation)
//            }
//            "Conversion" -> {
//                findNavController().navigate(R.id.action_from_change_to_conversion)
//            }
//            "HistoricalRates" ->{
//                findNavController().navigate(R.id.action_from_change_to_historical)
//            }
//        }


// Update base currency
private fun updateBaseCurrency(selectedCurrency: String) {
    Log.i(TAG, "updateBaseCurrency: $selectedCurrency")
    val updateBase = BaseCurrencyModel(1, selectedCurrency)
    mDatabaseViewModel.updateBaseCurrency(updateBase)
}
}
