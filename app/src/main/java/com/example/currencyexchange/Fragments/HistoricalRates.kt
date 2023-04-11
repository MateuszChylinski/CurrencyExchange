package com.example.currencyexchange.Fragments

import androidx.fragment.app.Fragment

class HistoricalRates : Fragment() {
//    //  View Binding
//    private var _binding: FragmentHistoricalRatesBinding? = null
//    private val mBinding get() = _binding!!
//
//    // Instances
//    private val mApiInstance = ApiServices.getInstance()
//    private var mDatabaseInstance: CurrencyDatabaseRepository? = null
//
//    //  View Model
//    lateinit var mViewModel: HistoricalViewModel
//
//    //  Variables
//    private val TAG = "HistoricalRates"
//    private var mDate: String = "default"
//    private var mBaseCurrency = "default"
//    private var mIsInit = false
//    private var mIsTouched = false
//    private var mHistoricalAdapter: HistoricalAdapter? = null
//
//    private val mCalendar = Calendar.getInstance()
//    private var mCurrencyList: MutableList<CurrencyNamesModel> = arrayListOf()
//    private var mAlLCurrencies: MutableList<CurrencyNamesModel> = mutableListOf()
//    private var mIsRefreshed: Boolean = false
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHistoricalRatesBinding.inflate(inflater, container, false)
//        val view = mBinding.root
//
//        mDatabaseInstance = (activity?.application as CurrencyApplication).repository
//
//        mViewModel = ViewModelProvider(
//            this,
//            HistoricalFactory(CurrencyRetrofitRepository(mApiInstance), mDatabaseInstance!!)
//        ).get(HistoricalViewModel::class.java)
//
////        mViewModel.mBaseCurrency.observe(
////            requireActivity()){
////                mBaseCurrency = it
////            }
////        mViewModel.currencyList.observe(requireActivity()){
////            mCurrencyList.addAll(it)
////            mAlLCurrencies.addAll(it)
////        }
//        mViewModel.historicalData.observe(requireActivity()) {
//            mHistoricalAdapter = HistoricalAdapter()
//            mHistoricalAdapter?.setData(((it?.rates ?: mutableMapOf()) as HashMap<String, Double>))
//            mBinding.historicalRv.layoutManager = LinearLayoutManager(this.context)
//            mBinding.historicalRv.adapter = mHistoricalAdapter
//        }
//
//        mBinding.historicalRefreshContainer.setOnRefreshListener {
//            mIsRefreshed = true
////            mBaseCurrency = mViewModel.getBaseCurrency()
//            mViewModel.clearApiResponse()
//
//            setDefaultVisibility()
//            mBinding.historicalRefreshContainer.isRefreshing = false
//        }
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        mCalendar.set(1999, 2, 1)
//        mBinding.historicalDt.minDate = mCalendar.timeInMillis
//        mBinding.historicalDt.maxDate = Calendar.getInstance().timeInMillis
//
//        mBinding.historicalChangeBaseIcon.setOnClickListener {
//            val mFragmentTagViewModel: FragmentTagViewModel by viewModels(
//                ownerProducer = {requireParentFragment()})
//            mFragmentTagViewModel.setMoveFlag(true)
//        }
//        setDefaultVisibility()
//    }
//
//    // Prepare default visibility - it is mainly needed after user will refresh the layout
//    private fun setDefaultVisibility() {
//        /** If user refreshed layout uncheck every position in ListView */
//        if (mIsRefreshed) {
//            for (i in 0 until mBinding.historicalSymbolsLv.checkedItemCount) {
//                mBinding.historicalSymbolsLv.setItemChecked(i, false)
//            }
//        }
//        mBinding.historicalInfo.visibility = View.VISIBLE
//        mBinding.historicalDt.visibility = View.VISIBLE
//        mBinding.historicalSaveDate.visibility = View.VISIBLE
//
//        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
//        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
//        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
//        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
//        mBinding.historicalChangeBase.visibility = View.INVISIBLE
//        mBinding.historicalBaseTv.visibility = View.INVISIBLE
//        mBinding.historicalDateTv.visibility = View.INVISIBLE
//
//        mBinding.historicalDateTv.visibility = View.INVISIBLE
//        mBinding.historicalRv.visibility = View.INVISIBLE
//
//        mBinding.historicalSaveDate.setOnClickListener {
//            getDate()
//        }
//    }
//
//    //  Get picked date, and store it in mDate variable in format of 'yyyy-mm-dd'
//    @SuppressLint("SimpleDateFormat")
//    private fun getDate() {
//        val cal = Calendar.getInstance()
//        val sdf = SimpleDateFormat("yyyy-MM-dd")
//        cal.set(Calendar.YEAR, mBinding.historicalDt.year)
//        cal.set(Calendar.MONTH, mBinding.historicalDt.month)
//        cal.set(Calendar.DATE, mBinding.historicalDt.dayOfMonth)
//        mDate = sdf.format(cal.time).toString()
//        mViewModel.date = mDate
//        setVisibilityToLv()
//    }
//
//    //  Prepare views to display ListView. Make unneeded views invisible
//    private fun setVisibilityToLv() {
//        mBinding.historicalInfo.visibility = View.INVISIBLE
//        mBinding.historicalDt.visibility = View.INVISIBLE
//        mBinding.historicalSaveDate.visibility = View.INVISIBLE
//
//        mBinding.historicalSelectInfo.visibility = View.VISIBLE
//        mBinding.historicalSymbolsLv.visibility = View.VISIBLE
//        mBinding.historicalSaveSymbols.visibility = View.VISIBLE
//        mBinding.historicalChangeInfo.visibility = View.VISIBLE
//        mBinding.historicalChangeBase.visibility = View.VISIBLE
//        mBinding.historicalBaseTv.visibility = View.VISIBLE
//        mBinding.historicalDateTv.visibility = View.VISIBLE
//
//        mBinding.historicalDateTv.text = String.format(getString(R.string.formatted_date), mDate)
//        mBinding.historicalBaseTv.text = String.format(getString(R.string.formatted_base_currency), mBaseCurrency)
//        deleteBaseFromTheList(mCurrencyList)
//    }
//
//    // Delete base currency from the list, so it will not be presented in Spinner, and ListView
//    private fun deleteBaseFromTheList(list: MutableList<CurrencyNamesModel>) {
//        if (list.toString().contains(mBaseCurrency)) {
//            val index = list.indices.find { list[it].toString() == mBaseCurrency }
//            list.removeAt(index!!)
//        }
//        if (!mIsInit) {
//            setupSpinner(list)
//            setupListView(list)
//            mIsInit = true
//        }
//    }
//
//    // Prepare spinner to display available currencies to change base currency, but only temporary. It will NOT affect the database.
//    private fun setupSpinner(currencies: MutableList<CurrencyNamesModel>) {
//        val adapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, currencies)
//        mBinding.historicalChangeBase.adapter = adapter
//        mBinding.historicalChangeBase.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    /** By using boolean variable: "mIsTouched" we can avoid self picking first value from the spinner   */
//                    if (mIsTouched) {
//                        mBaseCurrency = currencies[p2].toString()
//
//                        mBinding.historicalBaseTv.text =
//                            String.format(getString(R.string.formatted_base_currency), mBaseCurrency)
//                        currencies.clear()
//                        currencies.addAll(mAlLCurrencies)
//
//                        deleteBaseFromTheList(currencies)
//                        setupListView(currencies)
//                        adapter.notifyDataSetChanged()
//                    } else {
//                        mIsTouched = true
//                    }
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    Log.i(TAG, "onNothingSelected in historical spinner ")
//                }
//            }
//    }
//
//    //Prepare ListView to display available currencies to pick up, as a reference to historical rates of selected base currency
//    private fun setupListView(list: MutableList<CurrencyNamesModel>) {
//        val currencies: MutableList<CurrencyNamesModel> = mutableListOf()
//        val adapter =
//            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_multiple_choice, list)
//
//        Toast.makeText(
//            activity,
//            getString(R.string.select_up_to_30_currencies),
//            Toast.LENGTH_SHORT
//        ).show()
//
//        mBinding.historicalSymbolsLv.adapter = adapter
//        mBinding.historicalSymbolsLv.choiceMode = ListView.CHOICE_MODE_MULTIPLE
//        mBinding.historicalSymbolsLv.onItemClickListener =
//            object : AdapterView.OnItemClickListener {
//                override fun onItemClick(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//
//                    /** After every click, check if total selected amount of symbols is <= 30.
//                     * If user will try to select more than 30 symbols, inform him that he can't select more than 30
//                     * The limitation is result from not getting data from the server. Probably because of the retrofit wait time limit
//                     * **/
//
//                    if (mBinding.historicalSymbolsLv.checkedItemCount > 30) {
//                        Toast.makeText(
//                            requireContext(),
//                            getString(R.string.cant_select_more_currencies),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        mBinding.historicalSymbolsLv.setItemChecked(position, false)
//                    }
//                }
//            }
//        mBinding.historicalSaveSymbols.setOnClickListener {
//            // Check if the fragment was refreshed (by SwiperRefreshLayout) if yes, clear currencies list.
//            /** If this list will not be cleared, then after refresh, previously picked currencies will be pushed to the api call.  */
//            if (mIsRefreshed) {
//                currencies.clear()
//            }
//
//            /** Add to the created list all of the checked symbols. Next function will convert them into String */
//            for (i in 0 until list.size) {
//                if (mBinding.historicalSymbolsLv.isItemChecked(i)) {
//                    currencies.add(list[i])
//                }
//            }
//            getCurrencies(currencies)
//        }
//    }
//
//    /*  Fetch data from the api, and observe it.
//     After whole data is fetched, pass it into adapter which will display it in RecyclerView    */
//    private fun getCurrencies(list: MutableList<CurrencyNamesModel>) {
//        mViewModel.fetchHistoricalData(mBaseCurrency, list.joinToString(separator = ", "))
//        prepareViewsForRv()
//    }
//
//    //  Prepare views to display RecyclerView. Delete unneeded views
//    private fun prepareViewsForRv() {
//        mBinding.historicalSelectInfo.visibility = View.INVISIBLE
//        mBinding.historicalSymbolsLv.visibility = View.INVISIBLE
//        mBinding.historicalSaveSymbols.visibility = View.INVISIBLE
//        mBinding.historicalChangeInfo.visibility = View.INVISIBLE
//        mBinding.historicalChangeBase.visibility = View.INVISIBLE
//
//        mBinding.historicalDateTv.visibility = View.VISIBLE
//        mBinding.historicalRv.visibility = View.VISIBLE
//    }
}

