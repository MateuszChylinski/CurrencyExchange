package com.example.currencyexchange.Fragments

import androidx.fragment.app.Fragment

class Conversion : Fragment() {
//    private val TAG = "Conversion"
//
//    private var mConversionBinding: FragmentConversionBinding? = null
//    private val mBinding get() = mConversionBinding!!
//
//    private lateinit var mViewModel: ConversionViewModel
//    private var mApiInstance = ApiServices.getInstance()
//    private var mDatabaseInstance: CurrencyDatabaseRepository? = null
//
//    private var mBaseCurrency: String = "default"
//    private var mDesiredCurrency: String = "default"
//    private var mCurrencyList: MutableList<CurrencyNamesModel> = mutableListOf()
//
////    override fun onCreateView(
////        inflater: LayoutInflater, container: ViewGroup?,
////        savedInstanceState: Bundle?
////    ): View {
//
//
////        mConversionBinding = FragmentConversionBinding.inflate(layoutInflater)
////        val view = mBinding.root
////
////        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
////        mViewModel = ViewModelProvider(
////            this,
////            ConversionFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
////        )[ConversionViewModel::class.java]
////
//
////        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
////            mBaseCurrency = it
////            mBinding.conversionFromTv.text =
////                String.format(getString(R.string.formatted_from), mBaseCurrency)
////        })
////        /** The reason why I'm creating two lists with all of the currencies,
////         *  is because the 'mCurrencyList' is will NOT contain base currency, and desired currency.
////         *  In other hand, the 'mAllCurrencies' are not changed while program is running,
////         *  so it can provide new list of currencies to the 'mCurrencyList'*/
////        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
////            mCurrencyList.addAll(it)
////            mAllCurrencies.addAll(it)
////            prepareFromSpinner(mCurrencyList)
////            prepareToSpinner(mCurrencyList)
////        })
//
//////        /** Observe base currency from ViewModel. */
//////        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
//////            mBaseCurrency = it
//////            mBinding.conversionFromTv.text =
//////                String.format(getString(R.string.formatted_from), mBaseCurrency)
//////        })
//////
//////        /** Observe all available currencies, and pass them to the 'mCurrencyList'.  */
//////        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
//////            if (mCurrencyList.isEmpty()) {
//////                mCurrencyList.addAll(it)
//////            }
//////            deleteBaseFromSpinner(mCurrencyList)
//////        })
////        /**  Observe response from the api call, and display it in TextView.    */
//
////        mViewModel.conversionResult.observe(viewLifecycleOwner, Observer {
////            mBinding.conversionConvertedData.visibility = View.VISIBLE
////            mBinding.conversionConvertedData.text = String.format(
////                getString(R.string.formatted_you_will_receive),
////                it?.result, it?.query?.to
////            )
////        })
////        return view
////    }
////
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////        mBinding.conversionChangeBaseCurrency.setOnClickListener {
////            setFragmentResult("request_key", bundleOf("fragment_name" to TAG))
////            findNavController().navigate(R.id.action_from_base_to_change)
////        }
//
////        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
////            getValueFromEditText()
////        })
////
////        // Refresh layout
////        mBinding.conversionRefreshContainer.setOnRefreshListener {
////            // Clear variables
////            mCurrencyList.clear()
////            mCurrencyList.addAll(mAllCurrencies)
////            mDesiredCurrency = String()
////
////            mIsRefreshed = true
////            mBaseCurrency = mViewModel.getBaseCurrency()
////            defaultViewsSetup()
////            mBinding.conversionRefreshContainer.isRefreshing = false
////        }
//
////
////        /** Retrieve provided amount in EditText, and make an api call, based on:
////         *  base currency, desired currency, and amount*/
//////        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
//////            getValueFromEditText()
//////        })
//////
//////        /** Refresh layout UI*/
//////        mBinding.conversionRefreshContainer.setOnRefreshListener {
//////            mDesiredCurrency = String()
//////
//////            mBaseCurrency = mViewModel.getBaseCurrency()
//////            defaultViewsSetup(mCurrencyList)
//////            mBinding.conversionRefreshContainer.isRefreshing = false
//////        }
//
////    }
////
////    /**  After refreshing the layout, clear text in convertedData TextView, and make it invisible.  */
////    private fun defaultViewsSetup() {
////        if (mIsRefreshed) {
////            mBinding.conversionEnterValue.text.clear()
////            mBinding.conversionConvertedData.text = ""
////            mBinding.conversionConvertedData.visibility = View.INVISIBLE
////
////            mBinding.conversionToTv.text = getString(R.string.currency_name)
////            mBinding.conversionFromTv.text =
////                String.format(getString(R.string.formatted_from), mBaseCurrency)
////            deleteBaseFromSpinner(mCurrencyList)
////        }
////    }
////
////    /** Check if given list contains base currency, and desired currency (if user have already picked one).
////     * If list contains these currencies, delete it, so user will not see them in spinner anymore.*/
////    private fun deleteBaseFromSpinner(
////        list: MutableList<CurrencyNamesModel>
////    ) {
////        if (mDesiredCurrency != "default") {
////            val desiredIndex = list.indices.find { list[it].toString() == mDesiredCurrency }
////            desiredIndex?.let { it -> list.removeAt(it) }
////        }
////        val baseIndex = list.indices.find { list[it].toString() == mBaseCurrency }
////        baseIndex?.let { it -> list.removeAt(it) }
////
////        prepareFromSpinner(list)
////        prepareToSpinner(list)
////    }
////
////    private fun prepareFromSpinner(currencyList: MutableList<CurrencyNamesModel>) {
////        var isTouched = false
////
////        val fromAdapter =
////            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
////        mBinding.conversionFromSpinner.adapter = fromAdapter
////        mBinding.conversionFromSpinner.onItemSelectedListener =
////            object : AdapterView.OnItemSelectedListener {
////                override fun onItemSelected(
////                    parent: AdapterView<*>?,
////                    view: View?,
////                    position: Int,
////                    id: Long
////                ) {
////                    if (isTouched) {
////                        isTouched = false
////                        mBaseCurrency = currencyList[position].toString()
////                        mBinding.conversionFromTv.text =
////                            String.format(getString(R.string.formatted_from), mBaseCurrency)
////                        deleteBaseFromSpinner(currencyList)
////                    } else {
////                        isTouched = true
////                    }
////                }
////
////                override fun onNothingSelected(parent: AdapterView<*>?) {
////                    Log.i(TAG, "onNothingSelected in 'from' spinner")
////                }
////            }
////    }
////
////    private fun prepareToSpinner(currencyList: MutableList<CurrencyNamesModel>) {
////        var isTouched = false
////        val toAdapter =
////            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
////        mBinding.conversionToSpinner.adapter = toAdapter
////        mBinding.conversionToSpinner.onItemSelectedListener =
////            object : AdapterView.OnItemSelectedListener {
////                override fun onItemSelected(
////                    parent: AdapterView<*>?,
////                    view: View?,
////                    position: Int,
////                    id: Long
////                ) {
////                    if (isTouched) {
////                        isTouched = false
////                        mDesiredCurrency = currencyList[position].toString()
////                        mBinding.conversionToTv.text =
////                            String.format(getString(R.string.formatted_to), mDesiredCurrency)
////
////                        deleteBaseFromSpinner(currencyList)
////                    } else {
////                        isTouched = true
////                    }
////                }
////
////                override fun onNothingSelected(parent: AdapterView<*>?) {
////                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
////                }
////            }
////    }
////
////    /** get given value from EditText, and if it contains any value, perform an api call, and fetch the data.
////     * if base/desired currency are empty, or the EditText does not contain any value, inform user to complete data*/
////    private fun getValueFromEditText() {
////        val mAmount = mBinding.conversionEnterValue.text.toString()
////        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mAmount.isNotEmpty()) {
////            mViewModel.conversionCall(mBaseCurrency, mDesiredCurrency, mAmount)
////        } else {
////            Toast.makeText(
////                requireActivity(),
////                getString(R.string.select_desired_currency),
////                Toast.LENGTH_LONG
////            ).show()
////        }
////    }
//
//////    private fun getValueFromEditText() {
//////        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mBinding.conversionEnterValue.text.toString()
//////                .isNotEmpty()
//////        ) {
//////            mViewModel.conversionCall(
//////                mBaseCurrency,
//////                mDesiredCurrency,
//////                mBinding.conversionEnterValue.text.toString()
//////            )
//////        } else {
//////            Toast.makeText(
//////                requireActivity(),
//////                getString(R.string.select_desired_currency),
//////                Toast.LENGTH_LONG
//////            ).show()
//////        }
//////    }
//
//        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
//        mViewModel = ViewModelProvider(
//            this,
//            ConversionFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
//        )[ConversionViewModel::class.java]
//
//        /** Observe base currency from ViewModel. */
//        mViewModel.baseCurrency.observe(viewLifecycleOwner, Observer {
//            mBaseCurrency = it
//            mBinding.conversionFromTv.text =
//                String.format(getString(R.string.formatted_from), mBaseCurrency)
//        })
//
//        /** Observe all available currencies, and pass them to the 'mCurrencyList'.  */
//        mViewModel.currencyList.observe(viewLifecycleOwner, Observer {
//            if (mCurrencyList.isEmpty()) {
//                mCurrencyList.addAll(it)
//            }
//            deleteBaseFromSpinner(mCurrencyList)
//        })
//        /**  Observe response from the api call, and display it in TextView.    */
//        mViewModel.conversionResult.observe(viewLifecycleOwner, Observer {
//            mBinding.conversionConvertedData.visibility = View.VISIBLE
//            mBinding.conversionConvertedData.text = String.format(
//                getString(R.string.formatted_you_will_receive),
//                it?.result, it?.query?.to
//            )
//        })
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        /** Prepare a flag, to let ViewPager host fragment know, when it should navigate user to
//         * 'ChangeBaseCurrency' fragment.   */
//        mBinding.conversionChangeBaseCurrency.setOnClickListener {
//            val mFragmentVM: FragmentTagViewModel by viewModels(
//                ownerProducer = { requireParentFragment() })
//            mFragmentVM.setMoveFlag(true)
//        }
//
//        /** Retrieve provided amount in EditText, and make an api call, based on:
//         *  base currency, desired currency, and amount*/
//        mBinding.conversionConverseBtn.setOnClickListener(View.OnClickListener {
//            getValueFromEditText()
//        })
//
//        /** Refresh layout UI*/
//        mBinding.conversionRefreshContainer.setOnRefreshListener {
//            mDesiredCurrency = String()
//
//            mBaseCurrency = mViewModel.getBaseCurrency()
//            defaultViewsSetup(mCurrencyList)
//            mBinding.conversionRefreshContainer.isRefreshing = false
//        }
//    }
//
//    /** After refreshing the layout, copy list of all currencies, delete base currency from the list,
//     * clear TextView that display converted currency, make it invisible */
//    private fun defaultViewsSetup(list: MutableList<CurrencyNamesModel>) {
//        val currencyList = list.toMutableList()
//        deleteBaseFromSpinner(currencyList)
//
//        mBinding.conversionEnterValue.text.clear()
//        mBinding.conversionConvertedData.text = ""
//        mBinding.conversionConvertedData.visibility = View.INVISIBLE
//
//        mBinding.conversionToTv.text = getString(R.string.currency_name)
//        mBinding.conversionFromTv.text =
//            String.format(getString(R.string.formatted_from), mBaseCurrency)
//    }
//
//    /** Copy list of all currencies,
//     *  check if given list contains base currency, and desired currency (if user have already picked one).
//     *  If list contains these currencies, delete it, so user will not see them in spinner anymore.  */
//    private fun deleteBaseFromSpinner(list: MutableList<CurrencyNamesModel>) {
//        val currencyList = list.toMutableList()
//        if (mDesiredCurrency != "default") {
//            val desiredIndex =
//                currencyList.indices.find { currencyList[it].toString() == mDesiredCurrency }
//            desiredIndex?.let { it -> currencyList.removeAt(it) }
//        }
//        val baseIndex = currencyList.indices.find { currencyList[it].toString() == mBaseCurrency }
//        baseIndex?.let { it -> currencyList.removeAt(it) }
//
//        prepareFromSpinner(currencyList)
//        prepareToSpinner(currencyList)
//    }
//
//    /** Prepare 'from' spinner. This spinner allow to pick new, temporary base currency  */
//    private fun prepareFromSpinner(currencyList: MutableList<CurrencyNamesModel>) {
//        var isTouched = false
//
//        val fromAdapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
//        mBinding.conversionFromSpinner.adapter = fromAdapter
//        mBinding.conversionFromSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (isTouched) {
//                        isTouched = false
//                        mBaseCurrency = currencyList[position].toString()
//                        mBinding.conversionFromTv.text =
//                            String.format(getString(R.string.formatted_from), mBaseCurrency)
//                        deleteBaseFromSpinner(currencyList)
//                    } else {
//                        isTouched = true
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Log.i(TAG, "onNothingSelected in 'from' spinner")
//                }
//            }
//    }
//
//    /** Prepare 'to' spinner. Picked currency from this spinner, will be marked as desired currency in api call later on*/
//    private fun prepareToSpinner(currencyList: MutableList<CurrencyNamesModel>) {
//        var isTouched = false
//        val toAdapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencyList)
//        mBinding.conversionToSpinner.adapter = toAdapter
//        mBinding.conversionToSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    if (isTouched) {
//                        isTouched = false
//                        mDesiredCurrency = currencyList[position].toString()
//                        mBinding.conversionToTv.text =
//                            String.format(getString(R.string.formatted_to), mDesiredCurrency)
//
//                        deleteBaseFromSpinner(currencyList)
//                    } else {
//                        isTouched = true
//                    }
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Log.i(TAG, "onNothingSelected: NOTHING SELECTED!")
//                }
//            }
//    }
//
//    /** get given value from EditText, and if it contains any value, perform an api call, and fetch the data.
//     * if base/desired currency are empty, or the EditText does not contain any value, inform user to complete data*/
//    private fun getValueFromEditText() {
//        if (mBaseCurrency != "default" && mDesiredCurrency != "default" && mBinding.conversionEnterValue.text.toString()
//                .isNotEmpty()
//        ) {
//            mViewModel.conversionCall(
//                mBaseCurrency,
//                mDesiredCurrency,
//                mBinding.conversionEnterValue.text.toString()
//            )
//        } else {
//            Toast.makeText(
//                requireActivity(),
//                getString(R.string.select_desired_currency),
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }

}
