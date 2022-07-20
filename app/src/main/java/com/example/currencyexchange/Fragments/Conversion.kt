package com.example.currencyexchange.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.currencyexchange.API.ApiServices
import com.example.currencyexchange.Application.CurrencyApplication
import com.example.currencyexchange.Models.CurrencyDatabaseModel
import com.example.currencyexchange.R
import com.example.currencyexchange.ViewModels.CurrencyDatabaseFactory
import com.example.currencyexchange.ViewModels.CurrencyDatabaseViewModel

class Conversion : Fragment() {

    private val mRetrofitService = ApiServices.getInstance()
    private var mCurrenciesList = mutableListOf<String>()
    private val mViewModel: CurrencyDatabaseViewModel by activityViewModels{
        CurrencyDatabaseFactory((activity?.application as CurrencyApplication).repository)
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        testObserve()

        return inflater.inflate(R.layout.fragment_conversion, container, false)
    }

    private fun testObserve(){
        val currency = CurrencyDatabaseModel("to test")
        mViewModel.insertNewCurrency(currency)

        mViewModel.allCurrencies.observe(viewLifecycleOwner){
            curr -> curr.let {
            Log.i(TAG, "testObserve: $it")
        }
        }

//        currencyViewModel.allCurrencies.observe(this) {
//            currency.let {
//                Log.i(ContentValues.TAG, "onCreate: $it")
//            }
//        }
    }
}
