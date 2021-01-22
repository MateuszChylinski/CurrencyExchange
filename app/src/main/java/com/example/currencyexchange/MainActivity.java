package com.example.currencyexchange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.ViewModel.CurrencyViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CurrencyViewModel view = new ViewModelProvider(this).get(CurrencyViewModel.class);
        view.getCurrencyRateData().observe(this, new Observer<CurrencyModel>() {
            @Override
            public void onChanged(CurrencyModel currencyModels) {
                    Log.i(TAG, "onChanged: "+currencyModels.getBase());
            }
        });

    }
}