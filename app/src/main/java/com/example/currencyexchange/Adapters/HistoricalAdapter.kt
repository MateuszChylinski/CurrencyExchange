package com.example.currencyexchange.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoricalAdapter() : RecyclerView.Adapter<HistoricalAdapter.ViewHolder>() {
    private var mData = HashMap<String, Double>()

    fun setData(data: HashMap<String, Double>) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

    }
}