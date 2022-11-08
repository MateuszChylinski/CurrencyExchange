package com.example.currencyexchange.Adapters

import android.content.ContentValues.TAG
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.currencyexchange.Fragments.*
import java.lang.IllegalArgumentException
import kotlin.math.log


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
            else -> throw IllegalArgumentException("There's an error in FragmentStateAdapter while trying to create fragments")
        }
    }
}