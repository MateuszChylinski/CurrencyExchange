package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.LatestRowBinding

class LatestAdapter : RecyclerView.Adapter<LatestAdapter.ViewHolder>() {
    private var ratesData: Map<String, Double> = mapOf()

    fun setData(data: Map<String, Double>) {
        this.ratesData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LatestRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.latestCurrencyOrigin.text = ratesData.keys.toTypedArray()[position]

            binding.latestCurrencyValue.text =
                String.format("%.2f", ratesData.values.toTypedArray()[position])
        }
    }

    override fun getItemCount(): Int {
        return ratesData.size
    }

    inner class ViewHolder(val binding: LatestRowBinding) :
        RecyclerView.ViewHolder(binding.root)
}