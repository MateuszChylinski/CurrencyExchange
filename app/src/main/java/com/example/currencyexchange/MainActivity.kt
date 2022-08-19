package com.example.currencyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupFragments()
    }
    private fun setupFragments(){
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val fragments: ArrayList<Fragment> = arrayListOf(
            Fluctuation(),
            Latest(),
            Conversion(),
            HistoricalRates()
        )
        val fragmentsAdapter = ViewPagerAdapter(fragments, this)
        viewPager.adapter = fragmentsAdapter
    }
//TODO - consider getting all of the currencies from the latest fragment, and hardcode them whenever the database is created, so user can change default currency into desired one

//TODO Make another fragment where user can just update his desired basic currency.
//TODO Make another row in db with base currency
//TODO Add some kind of toolbar in every fragment where user can click on some icon, then go to the fragment where he can change the base currency

}