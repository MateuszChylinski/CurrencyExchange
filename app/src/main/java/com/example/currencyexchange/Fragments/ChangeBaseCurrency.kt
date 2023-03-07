package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyNamesModel
import com.example.currencyexchange.R
import com.example.currencyexchange.Repository.CurrencyDatabaseRepository
import com.example.currencyexchange.ViewModels.ChangeBaseFactory
import com.example.currencyexchange.ViewModels.ChangeBaseViewModel
import com.example.currencyexchange.databinding.FragmentChangeBaseCurrencyBinding
import kotlinx.coroutines.launch

class ChangeBaseCurrency : Fragment() {

    //  TAG
    private var TAG = "ChangeBaseCurrency"

    //  Variables
    private var mBaseCurrency: String = "default"
    private var mAllCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()
    private var mIsInit = false

    //  View binding
    private var _binding: FragmentChangeBaseCurrencyBinding? = null
    private val mBinding get() = _binding!!

    //  Instance
    private var mDatabaseInstance: CurrencyDatabaseRepository? = null
    private lateinit var mViewModel: ChangeBaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeBaseCurrencyBinding.inflate(inflater, container, false)
        val view = mBinding.root

        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
        mViewModel = ViewModelProvider(
            this,
            ChangeBaseFactory(mDatabaseInstance!!)
        ).get(ChangeBaseViewModel::class.java)

        /** Retrieve base currency from the database*/
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.getBaseCurrency()
                mViewModel.baseCurrency.collect { baseCurrency ->
                    when (baseCurrency) {
                        is DatabaseState.Success<*> -> {
                            mBinding.changeBaseCurrentBase.text = String.format(getString(R.string.formatted_current_base), baseCurrency.data)
                        }
                        is DatabaseState.Error<*> -> {
                            Log.i(TAG, "onCreateView: ERROR $baseCurrency")}
                    }
                }
            }
        }

        /**Retrieve list of all available currencies from the database*/
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mViewModel.getAllCurrencies()
                mViewModel.currencies.collect{ currenciesObj ->
                    when (currenciesObj){
                        is DatabaseState.Success<*> -> {

                        }
                        is DatabaseState.Error<*> -> {
                            Toast.makeText(requireContext(), "Failed to retrieve currencies", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return view
    }

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
        var isTouched = false

        val adapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, list)
        mBinding.changeBaseSelectCurrencySpinner.adapter = adapter
        mBinding.changeBaseSelectCurrencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isTouched) {
                        isTouched = false
                        mBaseCurrency = list[position].toString()
//                        mViewModel.updateBaseCurrency(mBaseCurrency)
                        moveToPreviousFragment()
                    } else {
                        isTouched = true
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
                }
            }
    }

    private fun moveToPreviousFragment() {
        val navCon = findNavController()
        navCon.previousBackStackEntry?.savedStateHandle?.set("key_flag", true)
        navCon.popBackStack()
    }
}
