package com.example.currencyexchange.RemoteDataSource.RetrofitBuilder;

import com.example.currencyexchange.RemoteDataSource.WebService.ApiCall;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    private static final String BASE_URL = "https://api.exchangeratesapi.io/";
    private static Retrofit instance;

    private static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    public static ApiCall getInterface() {
        return getInstance().create(ApiCall.class);
    }
}
