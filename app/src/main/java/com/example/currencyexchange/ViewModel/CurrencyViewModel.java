package com.example.currencyexchange.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.Repository.CurrencyRepository;

import java.util.List;

public class CurrencyViewModel extends AndroidViewModel {

    private CurrencyRepository mRepository;
    private MutableLiveData<CurrencyModel> mCurrencyRateData;

    public CurrencyViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CurrencyRepository();
        mCurrencyRateData = mRepository.getCurrentExchangeRates();
    }

    public MutableLiveData<CurrencyModel> getCurrencyRateData(){
        return mCurrencyRateData;
    }
}
