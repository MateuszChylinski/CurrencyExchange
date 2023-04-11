package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyexchange.API.DatabaseState
import com.example.currencyexchange.Adapters.LatestAdapter
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.*
import com.example.currencyexchange.databinding.FragmentLatestBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class Latest : Fragment() {

    private val TAG = "Latest"
    private val mViewModel: LatestViewModel by activityViewModels()

    private var mLatestBinding: FragmentLatestBinding? = null
    private val mBinding get() = mLatestBinding!!

//    private val mRetrofitService = ApiServices.getInstance()
//    private var mDatabaseServices: CurrencyDatabaseRepository? = null

    private var mBaseCurrency: String = ""
    private val mAdapter = LatestAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLatestBinding = FragmentLatestBinding.inflate(layoutInflater)
        val view = mBinding.root

        mBinding.latestRv.layoutManager = LinearLayoutManager(this.context)
        mBinding.latestRv.adapter = mAdapter




//        mDatabaseServices = (activity?.application as CurrencyApplication).repository
//        mViewModel =
//            ViewModelProvider(
//                this,
//                LatestFactory(CurrencyRetrofitRepository(mRetrofitService), mDatabaseServices!!)
//            ).get(LatestViewModel::class.java)


        /** Launch LAZY coroutine. It'll be triggered when needed.
         * In this case, whenever retrieved base currency (from database) will obtain specific value (!= "null")
         * Check if status of call was either Success/Error
         * If call was successful , push Map that contains data about currencies (their names, and rates) to the adapter, and display it in RecyclerView
         * If call was NOT successful, log error message   */

//        val apiCallCoroutine =
//            viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.LAZY) {
//                repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    mViewModel.fetchData(mBaseCurrency)
//                    mViewModel.latestRates.observe(viewLifecycleOwner, Observer { status ->
//                        when (status) {
//                            is ApiResult.Success<*> -> {
//                                mAdapter.setData(status.data?.latestRates!!)
//                            }
//                            is ApiResult.Error -> {
//                                Log.w(
//                                    TAG,
//                                    "onCreateView: Failed to get latest rates:\n${status.throwable}"
//                                )
//                            }
//                        }
//                    })
//                }
//            }
//
        /** Launch coroutine that will retrieve main data about currency.
         * Whenever retrieved base currency will NOT be "null", launch coroutine, that will trigger api call*/
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.baseCurrencyState.collect { currency ->
                    when (currency) {
                        is DatabaseState.Success -> {
                            mBaseCurrency = currency.data?.baseCurrency.toString()
                            mBinding.latestBase.text = String.format(
                                getString(R.string.formatted_base_currency),
                                mBaseCurrency
                            )
                            Log.i(TAG, "onCreateView: HALO ${currency.data?.baseCurrency}")
                        }
//                            mBinding.latestDate.text = String.format(
//                                getString(R.string.rates_from_date),
//                                currency.data?.ratesDate.toString()
//                            )
////                            if (mBaseCurrency != "null") {
////                                apiCallCoroutine.start()
////                            }
//                        }
                        is DatabaseState.Error -> {
                            Log.w(
                                TAG,
                                "onCreateView getBaseCurrency Failed to retrieve the base currency from the database:\n${currency.message}"
                            )
                        }
                    }

                }
            }
        }
//

//        /** Will be used when there's no internet connection    */
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                mViewModel.currenciesDataState.collect { currencyData ->
//                    when (currencyData) {
//                        is DatabaseState.Success -> {
//                            Log.i(TAG, "onCreateView: "+currencyData.data?.currencyData?.entries)
//                        }
//                        is DatabaseState.Error -> {
//                            Log.e(
//                                TAG,
//                                "onCreateView: Failed to get currency data\n${currencyData.message}",
//                            )
//                        }
//                    }
//                }
//            }
//        }

        /** By clicking on a icon, inside of the toolbar, set a move flag to the 'ChangeBaseCurrency' fragment
         *  where user can select new base currency which will be saved in database */
//        mBinding.latestChangeBase.setOnClickListener {
//            val testVM: FragmentTagViewModel by viewModels(
//                ownerProducer = { requireParentFragment() })
//            testVM.setMoveFlag(true)
//        }
        return view
    }
}