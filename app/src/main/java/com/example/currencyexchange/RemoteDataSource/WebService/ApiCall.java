package com.example.currencyexchange.RemoteDataSource.WebService;

import com.example.currencyexchange.Model.CurrencyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiCall {

    @GET("latest")
    Call<CurrencyModel> getCurrentExchangeRates();
}
