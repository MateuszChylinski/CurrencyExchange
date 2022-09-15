package com.example.currencyexchange

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.Adapters.ViewPagerAdapter
import com.example.currencyexchange.Fragments.*

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
        mFragments.add(Latest())
        mFragments.add(Conversion())
        mFragments.add(Fluctuation())
        mFragments.add(HistoricalRates())
//        mFragments.add(ChangeBaseCurrency())


        val fragmentsAdapter = ViewPagerAdapter(mFragments as ArrayList<Fragment>, this)
        mViewPager!!.adapter = fragmentsAdapter
    }
}

