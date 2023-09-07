package com.example.currencyexchange.Singletons

import javax.inject.Singleton

@Singleton
object DataModifier {
    fun deleteBaseCurrency(
        currencyList: MutableList<String>,
        baseCurrency: String,
        isListForSpinner: Boolean
    ): MutableList<String> {
        if (isListForSpinner) {
            val spinnerList: MutableList<String> = mutableListOf()

            if (spinnerList.contains("Select currency")) spinnerList.add(0, "Select currency")

            currencyList.forEach {
                if (it != baseCurrency) spinnerList.add(it)
            }
            return spinnerList
        }else{
            val listViewList: MutableList<String> = mutableListOf()

            currencyList.forEach{
                if (it != baseCurrency)  listViewList.add(it)
            }
        }
        return mutableListOf()
    }
}

//TODO prepare from/to spinners
//TODO get date from user
