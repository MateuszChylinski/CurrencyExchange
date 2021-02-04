package com.example.currencyexchange;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.currencyexchange.Adapter.CurrencyAdapter;
import com.example.currencyexchange.Model.CurrencyModel;
import com.example.currencyexchange.ViewModel.CurrencyViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Map<String, Float> mData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.currency_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        CurrencyAdapter adapter = new CurrencyAdapter();
        recyclerView.setAdapter(adapter);

        CurrencyViewModel view = new ViewModelProvider(this).get(CurrencyViewModel.class);
        view.getCurrencyRateData().observe(this, new Observer<Map<String, Float>>() {
            @Override
            public void onChanged(Map<String, Float> stringFloatMap) {

                mData.putAll(stringFloatMap);
                adapter.initiateData(mData);
            }
        });
    }
}