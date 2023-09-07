package com.example.currencyexchange.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.IllegalArgumentException

class PagerAdapter(fragment: Fragment, fragmentList: List<Fragment>) :
    FragmentStateAdapter(fragment) {

    private val NUM_PAGES: List<Fragment> = fragmentList

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NUM_PAGES[0]
            1 -> NUM_PAGES[1]
            2 -> NUM_PAGES[2]
            3 -> NUM_PAGES[3]
            4 -> NUM_PAGES[4]
            else -> throw IllegalArgumentException("Error in FragmentStateAdapter")
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return NUM_PAGES.size
    }
}