package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.R
import org.w3c.dom.Text
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
            LayoutInflater.from(parent.context).inflate(R.layout.historical_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mSymbol?.text = mData.keys.toTypedArray()[position]
        holder.mValue?.text = String.format("%.2f", mData.get(mData.keys.toTypedArray()[position]))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var mSymbol: TextView? = ItemView.findViewById(R.id.historical_items_symbol)
        var mValue: TextView? = ItemView.findViewById(R.id.historical_items_value)
    }
}