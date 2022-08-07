package com.example.currencyexchange.Adapters

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyexchange.Models.CurrencyModel
import com.example.currencyexchange.Models.Test
import com.example.currencyexchange.R

class FluctuationAdapter: RecyclerView.Adapter<FluctuationAdapter.ViewHolder>(){
    private var mCurrenciesNames: List<String> = arrayListOf()
    private var mCurrenciesStartRates: List<Double> = arrayListOf()
    private var mCurrenciesEndRates: List<Double> = arrayListOf()
    private var mCurrenciesChange: List<Double> = arrayListOf()
    private var mCurrenciesChangePct: List<Double> = arrayListOf()

    fun setData(currencyNames: List<String>, currencyStartRates: List<Double>, currencyEndRates: List<Double>,
    currencyChange: List<Double>, currencyChangePct: List<Double>){
        this.mCurrenciesNames = currencyNames
        this.mCurrenciesStartRates = currencyStartRates
        this.mCurrenciesEndRates = currencyEndRates
        this.mCurrenciesChange = currencyChange
        this.mCurrenciesChangePct = currencyChangePct
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fluctuation_items,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foreignCurrencyName.text = mCurrenciesNames[position]
        holder.startRate.text = mCurrenciesStartRates[position].toString()
        holder.endRate.text = mCurrenciesEndRates[position].toString()
        holder.change.text = mCurrenciesChange[position].toString()
        holder.changePct.text = mCurrenciesChangePct[position].toString()
    }

    override fun getItemCount(): Int {
        return mCurrenciesNames.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val foreignCurrencyName: TextView = ItemView.findViewById(R.id.fluctuation_rv_foreign_currency)
        val startRate: TextView = ItemView.findViewById(R.id.fluctuation_rv_start_rate)
        val endRate: TextView = ItemView.findViewById(R.id.fluctuation_rv_end_rate)
        val change: TextView = ItemView.findViewById(R.id.fluctuation_rv_change)
        val changePct: TextView = ItemView.findViewById(R.id.fluctuation_rv_change_pct)
    }
}