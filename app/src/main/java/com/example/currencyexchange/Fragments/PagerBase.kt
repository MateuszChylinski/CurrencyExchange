package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.currencyexchange.Adapters.PagerAdapter
import com.example.currencyexchange.R


class PagerBase : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pager_base, container, false)
        view.findViewById<ViewPager2>(R.id.pb_viewpager)?.let {
            val adapter = PagerAdapter(this)
            it.adapter = adapter
        }
        childFragmentManager.setFragmentResultListener(
            "request_key",
            this
        ) { requestKey, bundle ->
            val fragmentName = bundle.getString("fragment_name")
            Log.i(TAG, "onCreateView: ASDADSASDASDADSASDADSADSASD |||| $fragmentName")
            navigateToChangeBase(fragmentName!!)
        }

        return view
    }


    /**
    Find out which fragment called this fun, and navigate to change base fragment.
    Set the argument as a name of fragment that this fun was called from
     */

    fun navigateToChangeBase(fragmentName: String) {
        when (fragmentName){
            "Latest" ->{
                val provideLatest = PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideLatest)
                Log.i(TAG, "navigateToChangeBase: FROM LATEST")
            }
            "Fluctuation" ->{
                val provideFluctuation = PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideFluctuation)
                Log.i(TAG, "navigateToChangeBase: FROM FLUCTUATION")

            }
            "Conversion" ->{
                val provideConversion = PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideConversion)
                Log.i(TAG, "navigateToChangeBase: FROM CONVERSION")

            }
            "HistoricalRates" ->{
                val provideHistoricalRates = PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideHistoricalRates)
                Log.i(TAG, "navigateToChangeBase: FROM HISTORICAL")
            }
        }
    }
}