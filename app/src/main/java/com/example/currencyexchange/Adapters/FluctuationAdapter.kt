package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.Models.FluctuationRates
import com.example.currencyexchange.R
import com.example.currencyexchange.databinding.FluctuationItemsBinding

class FluctuationAdapter : RecyclerView.Adapter<FluctuationAdapter.ViewHolder>() {

    private var mData: Map<String, FluctuationRates> = mapOf()

    fun setData(data: Map<String, FluctuationRates>) {
        this.mData = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            FluctuationItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.fluctuationRvForeignCurrency.text = String.format(
                binding.fluctuationRvForeignCurrency.context.getString(R.string.formatted_base_currency),
                mData.keys.elementAt(position)
            )
            binding.fluctuationRvStartRate.text = String.format(
                binding.fluctuationRvStartRate.context.getString(R.string.formatted_start_rate),
                mData.entries.elementAt(position).value.start_rate
            )
            binding.fluctuationRvEndRate.text = String.format(
                binding.fluctuationRvEndRate.context.getString(R.string.formatted_end_rate),
                mData.entries.elementAt(position).value.end_rate
            )
            binding.fluctuationRvChange.text = String.format(
                binding.fluctuationRvChange.context.getString(R.string.formatted_change_rate),
                mData.entries.elementAt(position).value.change
            )
            binding.fluctuationRvChangePct.text = String.format(
                binding.fluctuationRvChangePct.context.getString(R.string.formatted_change_pct),
                mData.entries.elementAt(position).value.change_pct
            )
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(val binding: FluctuationItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}