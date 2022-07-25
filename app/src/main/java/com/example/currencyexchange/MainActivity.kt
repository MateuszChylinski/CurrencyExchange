package com.example.currencyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}