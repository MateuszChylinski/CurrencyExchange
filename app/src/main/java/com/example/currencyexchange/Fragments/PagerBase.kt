package com.example.currencyexchange.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.currencyexchange.Adapters.PagerAdapter
import com.example.currencyexchange.ViewModels.FragmentTagViewModel
import com.example.currencyexchange.databinding.FragmentPagerBaseBinding

import kotlinx.coroutines.launch
import java.sql.Time

class PagerBase : Fragment() {
    private var _binding: FragmentPagerBaseBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var pagerAdapter: PagerAdapter
    private val mFragmentsList =
        arrayListOf(Latest(), Conversion(), Fluctuation(), HistoricalRates(), TimeSeries())
    private val mViewModel: FragmentTagViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagerBaseBinding.inflate(inflater, container, false)
        val view = mBinding.root

        pagerAdapter = PagerAdapter(this, mFragmentsList)
        mBinding.pbViewpager.adapter = pagerAdapter
        lifecycleScope.launch {
            mViewModel.mIsMoved.observe(viewLifecycleOwner, Observer {
                if (it) {
                    val nav = findNavController()
                    nav.navigate(PagerBaseDirections.actionFromBaseToChange())
                    mViewModel.setMoveFlag(false)
                }
            })
        }
        return view
    }
}
