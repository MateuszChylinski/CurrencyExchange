package com.example.currencyexchange

import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Adapters.ViewPagerAdapter
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Fragments.Conversion
import com.example.currencyexchange.Fragments.Fluctuation
import com.example.currencyexchange.Fragments.HistoricalRates
import com.example.currencyexchange.Fragments.Latest
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel

class MainActivity : AppCompatActivity() {
    var mViewPager: ViewPager2? = null
    val mFragments: MutableList<Fragment> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFragments()
    }


    private fun setupFragments() {
        mViewPager = findViewById(R.id.viewPager)
        mFragments.add(HistoricalRates())
        mFragments.add(Fluctuation())
        mFragments.add(Latest())
        mFragments.add(Conversion())
        mFragments.add(ChangeBaseCurrency())

        val fragmentsAdapter = ViewPagerAdapter(mFragments as ArrayList<Fragment>, this)
        mViewPager!!.adapter = fragmentsAdapter
    }
}
//TODO Add some kind of toolbar in every fragment where user can click on some icon, then go to the fragment where he can change the base currency
//TODO Add fun to restart fragment on drag

