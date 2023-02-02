package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.FluctuationItemsBinding

class FluctuationAdapter : RecyclerView.Adapter<FluctuationAdapter.ViewHolder>() {
    private var mCurrenciesNames: List<String> = arrayListOf()
    private var mCurrenciesStartRates: List<Double> = arrayListOf()
    private var mCurrenciesEndRates: List<Double> = arrayListOf()
    private var mCurrenciesChange: List<Double> = arrayListOf()
    private var mCurrenciesChangePct: List<Double> = arrayListOf()


    fun setData(
        currencyNames: List<String>,
        currencyStartRates: List<Double>,
        currencyEndRates: List<Double>,
        currencyChange: List<Double>,
        currencyChangePct: List<Double>
    ) {
        this.mCurrenciesNames = currencyNames
        this.mCurrenciesStartRates = currencyStartRates
        this.mCurrenciesEndRates = currencyEndRates
        this.mCurrenciesChange = currencyChange
        this.mCurrenciesChangePct = currencyChangePct
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            FluctuationItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.fluctuationRvForeignCurrency.text =
            String.format("Currency name: %s", mCurrenciesNames[position])
        holder.binding.fluctuationRvStartRate.text =
            String.format("Currency start rate: %.2f".format(mCurrenciesStartRates[position]))
        holder.binding.fluctuationRvEndRate.text =
            String.format("Currency end rate: %.2f".format(mCurrenciesEndRates[position]))
        holder.binding.fluctuationRvChange.text =
            String.format("Currency change: %.2f".format(mCurrenciesChange[position]))
        holder.binding.fluctuationRvChangePct.text =
            String.format("Currency change pct: %.2f".format(mCurrenciesChangePct[position]))
    }

    override fun getItemCount(): Int {
        return mCurrenciesNames.size
    }

    inner class ViewHolder(var binding: FluctuationItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}