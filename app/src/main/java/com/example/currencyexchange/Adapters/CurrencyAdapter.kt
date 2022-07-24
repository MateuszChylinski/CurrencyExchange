package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.R
import org.w3c.dom.Text

class CurrencyAdapter() : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>(){
    private var data = HashMap<String, Double>()

    fun setData(data: HashMap<String, Double>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.currency_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.currencyOrigin.text = data.keys.toTypedArray()[position]
        holder.currencyValue.text = data.values.toTypedArray()[position].toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val currencyOrigin: TextView = ItemView.findViewById(R.id.latest_currency_origin)
        val currencyValue: TextView = ItemView.findViewById(R.id.latest_currency_value)
    }
}