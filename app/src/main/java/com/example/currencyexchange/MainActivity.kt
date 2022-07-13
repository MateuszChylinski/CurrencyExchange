package com.example.currencyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.APIs.ApiServices
import com.example.currencyexchange.Adapters.CurrencyAdapter
import com.example.currencyexchange.Adapters.ViewPagerAdapter
import com.example.currencyexchange.Fragments.Conversion
import com.example.currencyexchange.Fragments.Fluctuation
import com.example.currencyexchange.Fragments.HistoricalRates
import com.example.currencyexchange.Repository.CurrencyRepository
import com.example.currencyexchange.ViewModels.CurrencyViewModel
import com.example.currencyexchange.ViewModels.CurrencyViewModelFactory

class MainActivity : AppCompatActivity() {

    private val mRetrofitService = ApiServices.getInstance()
    private lateinit var mViewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fetchFromViewModel()
        test()



    }
    private fun test(){
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val fragments: ArrayList<Fragment> = arrayListOf(
            Conversion(),
            Fluctuation(),
            HistoricalRates()
        )
        val fragmentsAdapter = ViewPagerAdapter(fragments, this)
        viewPager.adapter = fragmentsAdapter
    }

//    TODO - move this fun to the latest rates fragment. It doesn't belong here anymore
//    private fun fetchFromViewModel() {
//        val mRecyclerView = findViewById<RecyclerView>(R.id.rv_test)
//        val mAdapter = CurrencyAdapter()
//        mRecyclerView.layoutManager = LinearLayoutManager(this)
//
//        mViewModel =
//            ViewModelProvider(this, CurrencyViewModelFactory(CurrencyRepository(mRetrofitService)))
//                .get(CurrencyViewModel::class.java)
//        mViewModel.fetchLatestRates()
//        mViewModel.currencyRatesList.observe(this, Observer {
//            mAdapter.setData(it.rates)
//            mRecyclerView.adapter = mAdapter
//        })
//        mViewModel.errorMessage.observe(this, Observer {
//        })
//    }


}
//TODO Add settings, when user can set the base currency
//TODO add another fragment, where user can decide which currencies he wants to see (maybe spinner, somewhere on the top? User could switch from there easily)