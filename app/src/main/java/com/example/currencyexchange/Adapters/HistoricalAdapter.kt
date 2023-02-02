package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.HistoricalItemsBinding
import java.util.*
import kotlin.collections.HashMap
import java.util.SortedMap as SortedMap

class HistoricalAdapter() : RecyclerView.Adapter<HistoricalAdapter.ViewHolder>() {
    private var mHashMap = HashMap<String, Double>()
    private var mData = mHashMap.toSortedMap()

    fun setData(data: SortedMap<String, Double>) {
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
            String.format("%.2f", mData.get(mData.keys.toTypedArray()[position]))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(var binding: HistoricalItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}