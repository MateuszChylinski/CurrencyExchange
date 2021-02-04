package com.example.currencyexchange.Repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.RemoteDataSource.RetrofitBuilder.RetrofitBuilder;
import com.example.currencyexchange.RemoteDataSource.WebService.ApiCall;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CurrencyRepository {

    private ApiCall mApi;

    public CurrencyRepository() {
        mApi = RetrofitBuilder.getInterface();
    }

    public MutableLiveData<Map<String, Float>> getCurrentExchangeRates() {
        MutableLiveData<Map<String, Float>> exchangeRatesDate = new MutableLiveData<>();

        mApi.getCurrentExchangeRates().enqueue(new Callback<CurrencyModel>() {
            @Override
            public void onResponse(Call<CurrencyModel> call, Response<CurrencyModel> response) {
                if (!response.isSuccessful()) {
                    Log.w(TAG, "onResponse: " + response.code());
                }
                exchangeRatesDate.setValue(response.body().getCurrenciesList());
            }

            @Override
            public void onFailure(Call<CurrencyModel> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });
        return exchangeRatesDate;
    }
}
