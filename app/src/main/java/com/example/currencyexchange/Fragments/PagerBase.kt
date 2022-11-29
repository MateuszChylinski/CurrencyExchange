package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.currencyexchange.Adapters.PagerAdapter
import com.example.currencyexchange.databinding.FragmentPagerBaseBinding


class PagerBase : Fragment() {
    private var _binding: FragmentPagerBaseBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBaseBinding.inflate(inflater, container, false)
        val view = mBinding.root
        mBinding.pbViewpager.let {
            it.adapter = PagerAdapter(this)
        }
        childFragmentManager.setFragmentResultListener(
            "request_key",
            this
        ) { requestKey, bundle ->
            val fragmentName = bundle.getString("fragment_name")
            navigateToChangeBase(fragmentName!!)
        }
        return view
    }

    /**
    Find out which fragment called this fun, and navigate to change base fragment.
    Set the argument as a name of fragment that this fun was called from
     */
    private fun navigateToChangeBase(fragmentName: String) {
        when (fragmentName) {
            "Latest" -> {
                val provideLatest =
                    PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideLatest)
                Log.i(TAG, "navigateToChangeBase: FROM LATEST")
            }
            "Fluctuation" -> {
                val provideFluctuation =
                    PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideFluctuation)
                Log.i(TAG, "navigateToChangeBase: FROM FLUCTUATION")

            }
            "Conversion" -> {
                val provideConversion =
                    PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideConversion)
                Log.i(TAG, "navigateToChangeBase: FROM CONVERSION")

            }
            "HistoricalRates" -> {
                val provideHistoricalRates =
                    PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideHistoricalRates)
                Log.i(TAG, "navigateToChangeBase: FROM HISTORICAL")
            }

            "test" -> {
                val provideLatest =
                    PagerBaseDirections.actionFromBaseToChange().setFragmentName(fragmentName)
                NavHostFragment.findNavController(this).navigate(provideLatest)
                Log.i(TAG, "navigateToChangeBase: FROM LATEST")
            }
        }
    }
}