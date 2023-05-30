package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Models.CurrenciesDatabaseMain
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.ChangeBaseViewModel
import com.example.currencyexchange.databinding.FragmentChangeBaseCurrencyBinding
import kotlinx.coroutines.launch

class ChangeBaseCurrency : Fragment() {
    private var TAG = "ChangeBaseCurrency"
    private var mBaseCurrency: String = ""

    private var _binding: FragmentChangeBaseCurrencyBinding? = null
    private val mBinding get() = _binding!!
    private val mViewModel: ChangeBaseViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeBaseCurrencyBinding.inflate(inflater, container, false)
        val view = mBinding.root

        /** Retrieve base currency from the database    */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrencyState.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success<*> -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.changeBaseCurrentBase.text = String.format(
                                getString(R.string.formatted_current_base),
                                mBaseCurrency
                            )
                        }
                        is DataWrapper.Error<*> -> {
                            Log.e(
                                TAG,
                                "ChangeBaseCurrency: failed to retrieve base currency from the database: ${currency.message}"
                            )
                        }
                    }
                }
            }
        }

        /** Retrieve list of all available currencies from the database */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.currencyNames.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            val currencyList: MutableList<String> = mutableListOf()
                            currency.data?.currencyData?.keys?.map {
                                currencyList.add(it)
                            }
                            deleteBaseFromList(currencyList)
                        }
                        is DataWrapper.Error -> {
                            Log.e(
                                TAG,
                                "onCreateView: Failed to get currency data from the ViewModel in $tag\n${currency.message}",
                            )
                        }
                    }
                }
            }
        }
        return view
    }

    // Delete base currency from the given list, so the user won't be able to select same currency as base twice
    private fun deleteBaseFromList(list: MutableList<String>) {
        if (list.toString().contains(mBaseCurrency)) {
            val index = list.indices.find { list[it] == mBaseCurrency }
            list.removeAt(index!!)
        }
        setupListView(list)
    }

    // Prepare ListView to display all available currencies besides the base currency
    private fun setupListView(list: MutableList<String>) {
        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)
        mBinding.changeBaseSelectCurrency.choiceMode = ListView.CHOICE_MODE_SINGLE
        mBinding.changeBaseSelectCurrency.adapter = adapter
        mBinding.changeBaseSelectCurrency.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mBaseCurrency = list[position]
                mViewModel.updateBaseCurrency(
                    CurrenciesDatabaseMain(
                        0,
                        mBaseCurrency
                    )
                )
                moveToPreviousFragment()
            }
        }
    }

    private fun moveToPreviousFragment() {
        val navCon = findNavController()
        navCon.previousBackStackEntry?.savedStateHandle?.set("key_flag", true)
        navCon.popBackStack()
    }
}
