package com.example.currencyexchange.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.currencyexchange.Fragments.*
import java.lang.IllegalArgumentException

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Latest()
            1 -> Conversion()
            2 -> Fluctuation()
            3 -> HistoricalRates()
            else -> throw IllegalArgumentException("Error in FragmentStateAdapter")
        }
    }
}