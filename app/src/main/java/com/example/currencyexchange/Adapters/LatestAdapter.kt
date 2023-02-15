package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.databinding.LatestRowBinding
import java.util.*

class LatestAdapter() : RecyclerView.Adapter<LatestAdapter.ViewHolder>() {
    private var data = sortedMapOf<String, Double>()

    fun setData(data: SortedMap<String, Double>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LatestRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.latestCurrencyOrigin.text = data.keys.toTypedArray()[position]
            binding.latestCurrencyValue.text = String.format("%.2f", data.values.toTypedArray()[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LatestRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}