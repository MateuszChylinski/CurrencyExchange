package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.HistoricalItemsBinding

class HistoricalAdapter : RecyclerView.Adapter<HistoricalAdapter.ViewHolder>() {
    private var mData: Map<String, Double> = mapOf()

    fun setData(data: Map<String, Double>) {
        this.mData = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            HistoricalItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.historicalItemsSymbol.text = mData.keys.toTypedArray()[position]
        holder.binding.historicalItemsValue.text =
            String.format("%.2f", mData[mData.keys.toTypedArray()[position]])
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    inner class ViewHolder(var binding: HistoricalItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}