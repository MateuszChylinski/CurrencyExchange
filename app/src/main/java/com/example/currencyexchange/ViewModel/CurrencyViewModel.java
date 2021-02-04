package com.example.currencyexchange.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.Repository.CurrencyRepository;

import java.util.List;
import java.util.Map;

public class CurrencyViewModel extends AndroidViewModel {

    private CurrencyRepository mRepository;
    private MutableLiveData<Map<String, Float>> mCurrencyRateData;

    public CurrencyViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CurrencyRepository();
        mCurrencyRateData = mRepository.getCurrentExchangeRates();
    }

    public MutableLiveData<Map<String, Float>> getCurrencyRateData() {
        return mCurrencyRateData;
    }
}
