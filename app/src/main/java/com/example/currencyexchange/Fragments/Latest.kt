package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.DataWrapper.DataWrapper
import com.example.currencyexchange.Adapters.LatestAdapter
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class Latest : Fragment() {

    private val TAG = "Latest"
    private val mViewModel: LatestViewModel by activityViewModels()
    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!
    private val mAdapter = LatestAdapter()

    private var mBaseCurrency: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLatestBinding = FragmentLatestBinding.inflate(layoutInflater)
        val view = mBinding.root

        mBinding.latestRv.layoutManager = LinearLayoutManager(this.context)
        mBinding.latestRv.adapter = mAdapter

        /** Launch LAZY coroutine. It'll be triggered when needed.
         * In this case, whenever retrieved base currency (from database) will obtain specific value (!= "null")
         * Check if status of call was either Success/Error
         * If call was successful , push Map that contains data about currencies (their names, and rates) to the adapter, and display it in RecyclerView
         * If call was NOT successful, log error message   */

        val apiCallCoroutine =
            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mViewModel.fetchData(mBaseCurrency)
                    mViewModel.latestRates.observe(viewLifecycleOwner, Observer { status ->
                        when (status) {
                            is DataWrapper.Success<*> -> {
                                mAdapter.setData(status.data?.latestRates!!)
                            }

                            is DataWrapper.Error -> {
                                Log.w(
                                    TAG,
                                    "onCreateView: Failed to get latest rates:\n${status.message}"
                                )
                            }
                        }
                    })
                }
            }

        /** Launch coroutine that will retrieve main data about currency.
         * Whenever retrieved base currency will NOT be "null", launch coroutine, that will trigger api call*/
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrencyState.collect { currency ->
                    when (currency) {
                        is DataWrapper.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.latestBase.text = String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                            mBinding.latestDate.text = String.format(
                                getString(R.string.rates_from_date),
                                currency.data?.ratesDate.toString()
                            )
                            if (mBaseCurrency != "null") {
                                apiCallCoroutine.start()
                            }
                        }

                        is DataWrapper.Error -> {
                            Log.w(
                                TAG,
                                "onCreateView getBaseCurrency Failed to retrieve the base currency from the database:\n${currency.message}"
                            )
                        }
                    }

                }
            }
        }

        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database */
        mBinding.latestChangeBase.setOnClickListener {
            val testVM: FragmentTagViewModel by viewModels(
                ownerProducer = { requireParentFragment() })
            testVM.setMoveFlag(true)
        }
        return view
    }
}